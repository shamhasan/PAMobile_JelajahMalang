package pg.mobile.projectpampricil.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pg.mobile.projectpampricil.data.Comment
import pg.mobile.projectpampricil.data.PlaceModel
import pg.mobile.projectpampricil.data.PlaceRepository


class PlaceViewModel(
    private val repository: PlaceRepository) : ViewModel() {

    private val _places = MutableStateFlow<List<PlaceModel>>(emptyList())
    val places: StateFlow<List<PlaceModel>> = _places

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites

    private val _comments = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())
    val comments: StateFlow<Map<String, List<Comment>>> = _comments

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _recommended = MutableStateFlow<List<PlaceModel>>(emptyList())
    val recommended: StateFlow<List<PlaceModel>> = _recommended
    init {
        // saat awal: load places + favorites dari API lalu hitung rekomendasi
        viewModelScope.launch {
            try {
                _places.value = repository.fetchPlaces()
                _favorites.value = repository.fetchFavorites()
                updateRecommendations()
            } catch (e: Exception) {
                // isi _error kalau mau
            }
        }
    }

    init {
        getPlaces()
        loadFavorites()
    }

    fun getPlaces() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _places.value = repository.fetchPlaces()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // 🔹 load komentar 1 tempat dari API
    fun loadComments(placeName: String) {
        viewModelScope.launch {
            try {
                val list = repository.fetchComments(placeName)
                _comments.value = _comments.value.toMutableMap().apply {
                    put(placeName, list)
                }
            } catch (_: Exception) { }
        }
    }

    // 🔹 user nambah komentar + rating
    fun addComment(placeName: String, text: String, rating: Int) {
        viewModelScope.launch {
            try {
                // 1. kirim komentar baru
                repository.addComment(placeName, text, rating)

                // 2. ambil semua komentar terbaru
                val allComments = repository.fetchComments(placeName)

                // 3. hitung rata-rata rating
                val avg = if (allComments.isEmpty()) 0.0
                else allComments.map { it.rating }.average()

                // 4. update rating ke places/{place}/rating
                repository.updateRating(placeName, avg)

                // 5. update state UI
                _comments.value = _comments.value.toMutableMap().apply {
                    put(placeName, allComments)
                }

                // 6. refresh places biar rating barunya kebaca di DetailScreen
                getPlaces()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
    fun loadFavorites() {
        viewModelScope.launch {
            try {
                _favorites.value = repository.fetchFavorites()
            } catch (_: Exception) { }
        }
    }


    fun toggleFavorite(placeName: String) {
        viewModelScope.launch {
            val current = _favorites.value.toMutableSet()
            val nowFav: Boolean

            if (current.contains(placeName)) {
                current.remove(placeName)
                nowFav = false
            } else {
                current.add(placeName)
                nowFav = true
            }

            _favorites.value = current

            updateRecommendations()


            try {
                repository.setFavorite(placeName, nowFav)
            } catch (e: Exception) {

            }
        }
    }

    private fun updateRecommendations() {
        val allPlaces = _places.value
        val favNames = _favorites.value

        if (favNames.isEmpty()) {
            _recommended.value = emptyList()
            return
        }

        // theme tempat yang difavoritkan
        val favoredThemes: Set<String> = allPlaces
            .filter { place -> favNames.contains(place.name) }
            .map { place -> place.theme }   // pastikan theme: String, bukan String?
            .toSet()

        // tempat lain dengan theme yang sama, tapi belum difavoritkan
        val recommendedList = allPlaces.filter { place ->
            place.name !in favNames && place.theme in favoredThemes
        }

        _recommended.value = recommendedList
    }

}
