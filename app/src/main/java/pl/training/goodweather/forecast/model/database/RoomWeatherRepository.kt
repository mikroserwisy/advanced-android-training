package pl.training.goodweather.forecast.model.database

import io.reactivex.Maybe
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import pl.training.goodweather.forecast.model.Weather
import pl.training.goodweather.forecast.model.database.DatabaseMappers.toDatabaseModel
import pl.training.goodweather.forecast.model.database.DatabaseMappers.toDomainModel

class RoomWeatherRepository(private val weatherDao: WeatherDao, private val scheduler: Scheduler) : WeatherRepository {

    override fun add(weather: Weather): Maybe<Weather> {
        return Maybe.fromCallable {
            val cityDb = toDatabaseModel(weather)
            weatherDao.add(cityDb)
            weatherDao.add(weather.forecast.map { toDatabaseModel(it, weather.id) })
            weather
        }
        .subscribeOn(scheduler)
    }

    override fun findByCityName(cityName: String): Maybe<Weather> =
        weatherDao.findByCityName(cityName)
            .map { toDomainModel(it) }
            .subscribeOn(scheduler)

}