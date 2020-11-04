package pl.training.goodweather.forecast.model

import io.reactivex.Observable
import io.reactivex.Scheduler
import pl.training.goodweather.forecast.RoomRepository
import pl.training.goodweather.forecast.model.api.ApiMappers
import pl.training.goodweather.forecast.model.api.WeatherProvider
import pl.training.goodweather.forecast.model.database.WeatherRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherInteractor @Inject constructor(private val weatherProvider: WeatherProvider,
                                            @RoomRepository
                                            private val weatherRepository: WeatherRepository,
                                            private val apiMappers: ApiMappers,
                                            private val scheduler: Scheduler) {

    fun getWeather(cityName: String): Observable<Weather> {
        val cachedWeather = weatherRepository.findByCityName(cityName)
            .toObservable()
        val refreshedWeather = weatherProvider.getWeather(cityName)
            .map(apiMappers::toDomainModel)
            .toObservable()
            .flatMap { weatherRepository.add(it).toObservable() }
        return Observable.concat(cachedWeather, refreshedWeather)
            .subscribeOn(scheduler)
    }

}