package eugen.leshchov.by.currencyconverter

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText



















import org.jetbrains.anko.alert
import org.slf4j.LoggerFactory


class MainActivity : AppCompatActivity() {

    private val PREFS_EXCHANGE_RATES = "exchangeRates"
    private val logger = LoggerFactory.getLogger("MainActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val btnCalc = findViewById<Button>(R.id.buttonCalculate) as Button
        btnCalc.setOnClickListener({
            val exchangeRates = getSharedPreferences(PREFS_EXCHANGE_RATES, Context.MODE_PRIVATE)
            val editEUR = findViewById<EditText>(R.id.eurOutput)
            val editUSD = findViewById<EditText>(R.id.usdOutput)
            val editRUB = findViewById<EditText>(R.id.rubOutput)
            val editCurrency = findViewById<EditText>(R.id.currencyBY)
            if (isConnected()) {
                updateCurrency()
            } else if (exchangeRates.contains("updateDate").not()) {
                alert("Check internet connection!").show()
            }
            try {
                val inputValue = editCurrency.text.toString().toFloat()
                editEUR.setText(" ".plus(inputValue / exchangeRates.getFloat("eur", 0F)))
                editUSD.setText(" ".plus(inputValue / exchangeRates.getFloat("usd", 0F)))
                editRUB.setText(" ".plus(inputValue / exchangeRates.getFloat("rub", 0F)))
            } catch (e: Exception) {
                alert("Incorrect data").show()
                logger.debug("Incorrect data")
            }
        })
    }


    private fun updateCurrency() {
        try {
            val exchangeRates = getSharedPreferences(PREFS_EXCHANGE_RATES, Context.MODE_PRIVATE)
            CurrencyClient().updateCurrencyRate("eur", exchangeRates)
            CurrencyClient().updateCurrencyRate("usd", exchangeRates)
            CurrencyClient().updateCurrencyRate("rub", exchangeRates)
        } catch (e: Exception) {
            logger.debug("update currency rates error")
        }
    }

    private fun isConnected(): Boolean {
        return try {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            activeNetwork != null && activeNetwork.isConnectedOrConnecting
        } catch (e: Exception) {
            logger.debug("check connection error")
            false
        }
    }
}
