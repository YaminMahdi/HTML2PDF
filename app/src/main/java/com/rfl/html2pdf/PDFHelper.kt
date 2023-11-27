package com.rfl.html2pdf

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import com.itextpdf.html2pdf.ConverterProperties
import com.itextpdf.html2pdf.HtmlConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


object PDFHelper {
    var title= "Due-Invoice"
    var orderId= ""
    var orderDate= ""
    var html= ""

    private var totalPrice = 0.0
    private val personList = mutableListOf<Item>()

    data class Item(var data: String, var quantity: Int, var price: Double)

    fun addItem(item: Item) {
        personList.add(item)
    }

    fun addItemList(itemList: List<Item>) {
        personList.addAll(itemList)
    }

    fun getPDF(context: Context, onEnd : (Intent) -> Unit){
        MainScope().launch(Dispatchers.IO){
            try {
                html = htmlTop
                personList.forEachIndexed { index, item ->
                    addHtmlTableRow(index+1, item)
                }
                val html = html + htmlBottom
                val htmlSource = File(context.cacheDir,"input.html")
                htmlSource.writeText(html)

                val pdfDest = File(context.cacheDir,"$title.pdf")


                val converterProperties = ConverterProperties()
                HtmlConverter.convertToPdf(
                    FileInputStream(htmlSource),
                    FileOutputStream(pdfDest), converterProperties
                )
                
                val photoURI = FileProvider.getUriForFile(
                    context,
                    context.applicationContext.packageName + ".provider",
                    pdfDest
                )

                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(photoURI, "application/pdf")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                withContext(Dispatchers.Main) {
                    onEnd.invoke(intent)
                }
            }
            catch (e: Exception){
                Log.d("TAG", "getPDF: ${e.message}")
            }
        }
    }

    private fun addHtmlTableRow(position: Int, item: Item) {
        val price = item.price * item.quantity
        totalPrice += price
        html += "<tr>" +
                "            <td>$position</td>" +
                "            <td class='item'>${item.data}</td>" +
                "            <td>${item.quantity}</td>" +
                "            <td >$price</td>" +
                "        </tr>"
    }

    private val htmlTop
        get() = "<!DOCTYPE html>" +
            "<html lang='en'>" +
            "<head>" +
            "    <title>$title</title>" +
            "    <style>" +
            "        h2 {" +
            "          color: maroon;" +
            "        }" +
            "        .data{" +
            "            width: 100%; " +
            "            border: 2px solid black; " +
            "            border-collapse: collapse;" +
            "        }" +
            "        .top{" +
            "            width: 100%; " +
            "        }" +
            "        .right{" +
            "            text-align: right;" +
            "        }" +
            "        .data{" +
            "            width: 100%; " +
            "            border: 2px solid black; " +
            "            border-collapse: collapse;" +
            "        }"+
            "        .data td, th{" +
            "            border-right: 2px solid black; " +
            "            border-left: 2px solid black;" +
            "            padding: 4px;" +
            "            vertical-align: baseline;" +
            "        }" +
            "        .tHead th{" +
            "            padding: 7px;" +
            "        }" +
            "        .data th{" +
            "            border-bottom: 2px solid black;" +
            "            border-top: 2px solid black;" +
            "        }"+
            "        .item{" +
            "            word-wrap: break-word; " +
            "            width: 20rem;" +
            "        }" +
            "        .price{" +
            "            min-width: 5rem;" +
            "        }" +
            "        .left{" +
            "            text-align: left;" +
            "        }" +
            "        .total{" +
            "            text-align: right;" +
            "            padding: 7px;" +
            "            padding-right: 20px;" +
            "        }" +
            "        </style>" +
            "</head>" +
            "<body>" +
            "    <h2><center>$title</center></h2>" +
            "    <table class='top'>" +
            "        <tr>" +
            "            <td><h4>Order Id: $orderId</h4></td>" +
            "            <td class='right'><h4>Order Date: $orderDate</h4></td>" +
            "        </tr>" +
            "    </table>"+
            "    <table class='data'>" +
            "        <tr class='boldL tHead'>" +
            "            <th>S/N</th>" +
            "            <th class='item'>Items</th>" +
            "            <th>Quantity</th>" +
            "            <th class='price'>Price</th>" +
            "        </tr>"

    private val htmlBottom
        get()= "<tr class='boldL left'>" +
            "            <th colspan='3' class='total'>Total Price</th>" +
            "            <th>$totalPrice</th>" +
            "        </tr>" +
            "    </table>" +
            "</body>" +
            "</html>"
}
