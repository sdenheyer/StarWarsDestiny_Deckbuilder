package com.stevedenheyer.starwarsdestinydeckbuilder.data

import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.OperatorUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.QueryUi
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.data.CardCache
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.data.ApiErrorResponse
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.data.ApiSuccessResponse
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.data.CardNetwork
import com.stevedenheyer.starwarsdestinydeckbuilder.di.IoDispatcher
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.Resource
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardFormatList
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardSetList
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CharacterCard
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Deck
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.OwnedCard
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Slot
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.repositories.CardRepository
import com.stevedenheyer.starwarsdestinydeckbuilder.utils.setCodeMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val cardCache: CardCache,
    private val cardNetwork: CardNetwork,
    private val coroutineScope: CoroutineScope,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : CardRepository {

    override fun getCardbyCode(code: String, forceRemoteUpdate: Boolean): Flow<Resource<Card?>> {
        return networkBoundResource(
            fetchFromLocal = { cardCache.getCardByCode(code) },
            shouldFetchFromRemote = {
                it == null ||
                        (forceRemoteUpdate) ||
                        (Date().time - (it.timestamp) > (it.expiry))
            },
            fetchFromRemote = { cardNetwork.getCardByCode(code) },
            processRemoteResponse = { },
            saveRemoteData = { cardCache.storeCards(listOf(it)) },
            onFetchFailed = { _, _ -> }

        ).flowOn(dispatcher)
    }

    override fun getCardSets(forceRemoteUpdate: Boolean): Flow<Resource<CardSetList>> {
        return networkBoundResource(
            fetchFromLocal = { cardCache.getCardSets() },
            shouldFetchFromRemote = {
                (it?.cardSets.isNullOrEmpty()) ||
                        (forceRemoteUpdate) ||
                        (Date().time - (it?.timestamp ?: 0L) > (it?.expiry ?: 0L))
            },
            fetchFromRemote = { cardNetwork.getCardSets() },
            processRemoteResponse = { },
            saveRemoteData = { cardCache.storeCardSets(it) },
            onFetchFailed = { _, _ -> }
        ).flowOn(dispatcher)
    }

    override fun getCardsBySet(
        code: String,
        forceRemoteUpdate: Boolean
    ): Flow<Resource<List<Card>>> {
        return networkBoundResource(
            fetchFromLocal = { cardCache.getCardsBySet(code) },
            shouldFetchFromRemote = {
                it.isNullOrEmpty() ||
                        (forceRemoteUpdate)
            },
            fetchFromRemote = { cardNetwork.getCardsBySet(code) },
            processRemoteResponse = { },
            saveRemoteData = { cardCache.storeCards(it) },
            onFetchFailed = { _, _ -> }
        ).flowOn(dispatcher)
    }

    override fun getCardFormats(forceRemoteUpdate: Boolean): Flow<Resource<CardFormatList>> {
        return networkBoundResource(
            fetchFromLocal = { cardCache.getFormats() },
            shouldFetchFromRemote = {
                it?.cardFormats.isNullOrEmpty() ||
                        (forceRemoteUpdate) ||
                        (Date().time - (it?.timestamp ?: 0L) > (it?.expiry ?: 0L))
            },
            fetchFromRemote = { cardNetwork.getFormats() },
            processRemoteResponse = { },
            saveRemoteData = { cardCache.storeFormats(it) },
            onFetchFailed = { _, _ -> }
        ).flowOn(dispatcher)
    }

    override fun findCards(query: QueryUi): Flow<Resource<List<Card>>> {
        return networkBoundResource(
            fetchFromLocal = {
                cardCache.findCards(query).map { cards ->
                    if (query.byFormat.isNotBlank()) {
                        val formats =
                            getCardFormats(false).first { it.status != Resource.Status.LOADING }
                       // Log.d("SWD", "formatsresrouce: ${formats.status}")
                        if (formats.status == Resource.Status.SUCCESS) {
                            cards.filter { card ->
                              //  Log.d("SWD", "formats: ${formats.data?.cardFormats}")
                                val format =
                                    formats.data?.cardFormats?.find { it.gameTypeCode == query.byFormat }
                                if (format == null) {
                                    true
                                } else {
                                    val cardCodes =
                                        listOf(card.code, *card.reprints.map { it.fetchCode() }.toTypedArray())
                                    val setCodes = listOf(
                                        card.setCode,
                                        *card.reprints.mapNotNull { code ->
                                            setCodeMap[code.fetchCode().substring(0, 2)]
                                        }.toTypedArray()
                                    )
                                    Log.d("SWD", "Checking codes: ${setCodes}")
                                    if (!(setCodes.any { it in format.includedSets }) || cardCodes.any { it in format.banned })
                                        false
                                    else {
                                        true
                                    }
                                }
                            }
                        } else {
                            cards

                        }
                    } else {
                        cards
                    }
                }
            },
            shouldFetchFromRemote = { true },
            fetchFromRemote = { cardNetwork.findCards(query) },
            processRemoteResponse = {},
            saveRemoteData = { cardCache.storeCards(it) },
            onFetchFailed = { _, _ -> }
        )
    }


    override suspend fun createDeck(deck: Deck) {
        cardCache.createDeck(deck)
    }

    override fun getAllDecks(): Flow<List<Deck>> = cardCache.getDecks()

    override suspend fun updateDeck(deck: Deck) {
        cardCache.updateDeck(deck)
    }

    override suspend fun updateDeck(deck: Deck, slot: Slot) {
        Log.d("SWD", "Writing new Deck: ${deck.battlefieldCardCode}")
        cardCache.updateDeck(deck, slot)
    }

    override suspend fun updateDeck(deck: Deck, char: CharacterCard) {
        cardCache.updateDeck(deck, char)
    }

    override suspend fun getDeck(deckName: String): Deck = cardCache.getDeck(deckName)

    override fun getOwnedCards(): Flow<List<OwnedCard>> = cardCache.getOwnedCards()

    override suspend fun insertOwnedCards(vararg cards: OwnedCard) =
        cardCache.storeOwnedCards(*cards)
}