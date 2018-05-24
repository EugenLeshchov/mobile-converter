package eugen.leshchov.by.currencyconverter

import android.content.SharedPreferences
import android.os.AsyncTask
import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.httpGet
import java.util.*


class CurrencyClient {

    private val idCurrencyMap: HashMap<String, String> = HashMap()

    data class Currency(
            val Cur_ID: Int,
            val Date: String,
            val Cur_Abbreviation: String,
            val Cur_Scale: Double,
            var Cur_Name: String,
            val Cur_OfficialRate: Double)

    init {
        idCurrencyMap["eur"] = "292"
        idCurrencyMap["usd"] = "145"
        idCurrencyMap["rub"] = "298"
    }

    fun updateCurrencyRate(nameCurrency: String, shared: SharedPreferences) {
        val (_, _, result) = "http://www.nbrb.by/API/ExRates/Rates/"
                .plus(idCurrencyMap[nameCurrency]).httpGet().responseString()
        val dto: Currency = Klaxon().parse<Currency>(result.get())!!
        shared.edit().putFloat(nameCurrency, (dto.Cur_OfficialRate / dto.Cur_Scale).toFloat())
                .apply()
        shared.edit().putString("updateDate", Calendar.getInstance().toString()).apply()
    }
}
