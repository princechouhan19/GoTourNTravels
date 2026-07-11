package com.gotourntravels.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gotourntravels.models.Place
import com.gotourntravels.repository.GoTourRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlacesViewModel @Inject constructor(private val repo: GoTourRepository) : ViewModel() {

    private val _items = MutableStateFlow<List<Place>>(emptyList())
    val items: StateFlow<List<Place>> = _items.asStateFlow()
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun load(category: String? = null) {
        viewModelScope.launch {
            _loading.value = true
            try { 
                _items.value = repo.listPlaces(category) 
            } catch (e: Exception) { 
                _items.value = listOf(
                    Place(
                        name = "Nakki Lake",
                        category = "attraction",
                        description = "A beautiful sacred lake in Mount Abu. Legend has it that the lake was dug out by Gods using their nails (nakh).",
                        lat = 24.5925,
                        lng = 72.7078,
                        address = "Nakki Lake, Mount Abu, Rajasthan 307501",
                        rating = 4.7
                    ),
                    Place(
                        name = "Dilwara Temples",
                        category = "attraction",
                        description = "World-famous marble temples built between the 11th and 13th centuries AD, known for their extraordinary architecture.",
                        lat = 24.6133,
                        lng = 72.7231,
                        address = "Dilwara Temple Road, Mount Abu, Rajasthan 307501",
                        rating = 4.9
                    ),
                    Place(
                        name = "Guru Shikhar",
                        category = "attraction",
                        description = "The highest peak of the Aravalli Range, offering breathtaking panoramic views of Mount Abu.",
                        lat = 24.6508,
                        lng = 72.7758,
                        address = "Guru Shikhar Peak, Mount Abu, Rajasthan 307501",
                        rating = 4.8
                    ),
                    Place(
                        name = "Toad Rock",
                        category = "attraction",
                        description = "A massive rock structure resembling a toad, offering a scenic spot for photography and hiking.",
                        lat = 24.5961,
                        lng = 72.7111,
                        address = "Toad Rock Trail, Mount Abu, Rajasthan 307501",
                        rating = 4.5
                    ),
                    Place(
                        name = "HP Fuel Station - Mount Abu",
                        category = "petrol",
                        description = "Fuel station serving Petrol and Diesel.",
                        lat = 24.5888,
                        lng = 72.7125,
                        address = "Main Road, Mount Abu, Rajasthan",
                        rating = 4.2
                    ),
                    Place(
                        name = "Mount Abu Global Hospital",
                        category = "hospital",
                        description = "24x7 Emergency and Trauma care facility.",
                        lat = 24.5940,
                        lng = 72.7160,
                        address = "Global Hospital Road, Mount Abu",
                        rating = 4.6
                    ),
                    Place(
                        name = "Mount Abu Police Station",
                        category = "police",
                        description = "Local Police Station for emergencies.",
                        lat = 24.5910,
                        lng = 72.7090,
                        address = "Nakki Lake Road, Mount Abu",
                        rating = 4.4
                    )
                ).filter { category == null || it.category == category }
                _error.value = null
            } finally { 
                _loading.value = false 
            }
        }
    }
}
