package com.rfl.html2pdf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PDFHelper.apply {
            title= "Due-Invoice"
            orderId ="OJF4638"
            orderDate ="27/11/2023"

            for (i in 0 ..90){
                addItem(PDFHelper.Item("f trtiorut tiruetioertuertue reter ert etoper eteoprt t",2,500.0))
            }

            getPDF(this@MainActivity){ pdfIntent->
                startActivity(pdfIntent)
            }
        }
    }
}