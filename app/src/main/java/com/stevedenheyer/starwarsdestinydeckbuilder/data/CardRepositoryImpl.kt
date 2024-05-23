package com.stevedenheyer.starwarsdestinydeckbuilder.data

import android.util.Log
import androidx.datastore.core.DataStore
import com.stevedenheyer.starwarsdestinydeckbuilder.UserSettings
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.QueryUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.SavedQueriesUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.SortState
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.SortUi
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.data.CardCache
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.data.ApiEmptyResponse
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
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.CardRepository
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.ICardCache
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.ICardNetwork
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardOrCode
import com.stevedenheyer.starwarsdestinydeckbuilder.utils.setCodeMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val cardCache: ICardCache,
    private val cardNetwork: ICardNetwork,
    private val dataStore: DataStore<UserSettings>,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : CardRepository {

    private val dateFormatter = DateTimeFormatter.RFC_1123_DATE_TIME

    override fun getCardByCode(code: String, forceRemoteUpdate: Boolean): Flow<Resource<Card?>> {
        return networkBoundResource(
            fetchFromLocal = { cardCache.getCardByCode(code) },
            shouldFetchFromRemote = {
                it == null ||
                        (forceRemoteUpdate) ||
                        (Date().time - (it.timestamp) > (it.expiry))
            },
            fetchFromRemote = {
                val date =  Date(it?.timestamp ?: 0L).toString().format(dateFormatter)
                cardNetwork.getCardByCode(date, code) },
            processRemoteResponse = { },
            saveRemoteData = { cardCache.storeCards(listOf(it)) },
            onFetchFailed = { _, _ -> }

        ).flowOn(dispatcher)
    }

    override fun getCardsByCodes(vararg values: CardOrCode): Flow<Resource<List<CardOrCode>>> = flow {
        val list = cardCache.getCardsByCodes(*values).toMutableList()
        if (list.size == values.size) {
            emit(Resource.success(data = list, isFromDB = true))
        } else {
            emit(Resource.loading(data = list))
            val needFromNetwork = values.filter { card -> card.fetchCode() !in list.map { it.fetchCode() } }.toMutableList()
            needFromNetwork.addAll(values.filter {
                if (it is CardOrCode.HasCode) true else {
                val card = (it as CardOrCode.HasCard).card
                Date().time - (card.timestamp) > (card.expiry)  }
            })
            needFromNetwork.forEach {
                when (val apiResource = cardNetwork.getCardByCode(0L.toString().format(dateFormatter), it.fetchCode()).first()) {  //If not in database, last modified not applied
                    is ApiSuccessResponse -> {
                        list.add(CardOrCode.HasCard(apiResource.body!!))
                        emit(Resource.loading(list))
                    }
                    is ApiErrorResponse -> {
                        emit(Resource.error(msg = apiResource.errorMessage, data = list))
                        return@flow
                    }
                    is ApiEmptyResponse -> {
                        emit(Resource.loading(list))
                    }
                }
            }
            emit(Resource.success(data = list))
        }
    }

    override fun getCardSets(forceRemoteUpdate: Boolean): Flow<Resource<CardSetList>> {
       // return flow { Resource.loading(CardSetList(0, 100000, emptyList())) }  testing...

        return networkBoundResource(
            fetchFromLocal = { cardCache.getCardSets() },
            shouldFetchFromRemote = {
                (it?.cardSets.isNullOrEmpty()) ||
                        (forceRemoteUpdate) ||
                        (Date().time - (it?.timestamp ?: 0L) > (it?.expiry
                            ?: (24 * 60 * 60 * 1000L)))
            },
            fetchFromRemote = {val date =  Date(it?.timestamp ?: 0L).toString().format(dateFormatter)

                cardNetwork.getCardSets(date) },
            processRemoteResponse = { },
            saveRemoteData = { cardCache.storeCardSets(it) },
            onFetchFailed = { _, _ -> }
        ).flowOn(dispatcher)
    }

    override fun getCardsBySet(
        code: String,
        forceRemoteUpdate: Boolean
    ): Flow<Resource<List<Card>>> {
        // return flow { Resource.loading(listOf<Card>()) }  Testing...

        return networkBoundResource(
            fetchFromLocal = { cardCache.getCardsBySet(code) },
            shouldFetchFromRemote = {
                it.isNullOrEmpty() ||
                        (forceRemoteUpdate)
            },
            fetchFromRemote = {
                val date = if (forceRemoteUpdate)
                    0L.toString().format(dateFormatter)
                else
                    it.let { cards ->
                    (cards?.map { card -> card.timestamp }?.minOrNull() ?: 0L).toString().format(dateFormatter)
                    }

                cardNetwork.getCardsBySet(date, code) },
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
                        (Date().time - (it?.timestamp ?: 0L) > (it?.expiry
                            ?: (24 * 60 * 60 * 1000L)))
            },
            fetchFromRemote = {val date =  Date(it?.timestamp ?: 0L).toString().format(dateFormatter)
             //   Log.d("SWD", "Timestamp: ${it?.timestamp} LastModifiedDate: $date")
                cardNetwork.getFormats(date) },
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
                       // Log.d("SWD", "formats resource: ${formats.status}")
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
                                  //  Log.d("SWD", "Checking codes: ${setCodes}")
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

    override fun fetchSavedQueries(): Flow<SavedQueriesUi> = dataStore.data.map {userSettings ->
        //Log.d("SWD", "Fucking protocol buffers: ${userSettings.cardNameQueriesList}")
        SavedQueriesUi(
            nameQueries = userSettings.cardNameQueriesList,
            textQueries = userSettings.cardTextQueriesList
        )
    }

    override suspend fun updateSavedNameQueries(newQuery: String) {
        dataStore.updateData { userSettings ->
            val queries = userSettings.cardNameQueriesList.toMutableList()
            if (newQuery.isNotBlank() && !queries.contains(newQuery))
                queries.add(newQuery)
            if (queries.size > 6)
                queries.removeFirst()
            userSettings.toBuilder().clearCardNameQueries().addAllCardNameQueries(queries).build()
        }
    }

    override suspend fun updateSavedTextQueries(newQuery: String) {
        dataStore.updateData { userSettings ->
            val queries = userSettings.cardTextQueriesList.toMutableList()
            if (newQuery.isNotBlank() && !queries.contains(newQuery))
                queries.add(newQuery)
            if (queries.size > 6)
                queries.removeFirst()
            userSettings.toBuilder().clearCardTextQueries().addAllCardTextQueries(queries).build()
        }
    }

    override suspend fun createDeck(deck: Deck) {
        cardCache.createDeck(deck)
    }

    override fun getAllDecks(): Flow<List<Deck>> = cardCache.getDecks()

    override suspend fun updateDeck(deck: Deck) {
        cardCache.updateDeck(deck)
    }

    override suspend fun updateDeck(deck: Deck, slot: Slot) {
      //  Log.d("SWD", "Writing new Deck: ${deck.battlefieldCardCode}")
        cardCache.updateDeck(deck, slot)
    }

    override suspend fun updateDeck(deck: Deck, char: CharacterCard) {
        cardCache.updateDeck(deck, char)
    }

    override suspend fun getDeck(deckName: String): Deck = cardCache.getDeck(deckName)

    override fun deleteDeck(deck: Deck) {
        cardCache.deleteDeck(deck)
    }

    override fun getOwnedCards(): Flow<List<OwnedCard>> = cardCache.getOwnedCards()

    override suspend fun insertOwnedCards(vararg cards: OwnedCard) =
        cardCache.storeOwnedCards(*cards)

    override fun sortStateFlow(): Flow<SortUi> = dataStore.data.map { userSettings ->
        SortUi(
            hideHero = userSettings.hideHero,
            hideVillain = userSettings.hideVillain,
            sortState = when (userSettings.sortBy) {
                UserSettings.SortBy.SORTBY_NAME -> SortState.NAME
                UserSettings.SortBy.SORTBY_SET -> SortState.SET
                UserSettings.SortBy.SORTBY_FACTION -> SortState.FACTION
                UserSettings.SortBy.SORTBY_POINTS_COST -> SortState.POINTS_COST
                else -> SortState.SET
            }
        )
    }

    override suspend fun setSortByState(sortState: SortState) {
        dataStore.updateData { currentSettings ->
            currentSettings.toBuilder().apply {
                when (sortState) {
                    SortState.HIDE_HERO -> setHideHero(!currentSettings.hideHero)
                    SortState.SHOW_VILLAIN -> setHideVillain(!currentSettings.hideVillain)
                    SortState.NAME -> setSortBy(UserSettings.SortBy.SORTBY_NAME)
                    SortState.SET -> setSortBy(UserSettings.SortBy.SORTBY_SET)
                    SortState.FACTION -> setSortBy(UserSettings.SortBy.SORTBY_FACTION)
                    SortState.POINTS_COST -> setSortBy(UserSettings.SortBy.SORTBY_POINTS_COST)
            }
            }.build()
        }
    }
}