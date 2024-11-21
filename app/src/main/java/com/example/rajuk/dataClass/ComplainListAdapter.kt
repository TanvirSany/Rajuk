package com.example.rajuk.dataClass

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.rajuk.R

class ComplainListAdapter (private val complains: List<Complain>) : RecyclerView.Adapter<ComplainListAdapter.ComplainViewHolder>() {

    class ComplainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

         val plotTypeTextView: TextView = itemView.findViewById(R.id.textViewPlotType)
         val addressTextView: TextView = itemView.findViewById(R.id.textViewAddress)
         val detailsTextView: TextView = itemView.findViewById(R.id.textViewDetails)
         val statusTextView: TextView = itemView.findViewById(R.id.textViewStatus)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.complain_list, parent, false)
        return ComplainViewHolder(view)
    }

    override fun getItemCount(): Int {
        return complains.size
    }

    override fun onBindViewHolder(holder: ComplainViewHolder, position: Int) {
        val complain = complains[position]
        holder.plotTypeTextView.text = "Plot Type: ${ complain.plotType }"
        holder.addressTextView.text = "Address: ${complain.houseNo}, ${complain.road}, ${complain.cityCorporationName}, ${complain.thanaName}"
        holder.detailsTextView.text = "Details: ${ complain.details }"
        holder.statusTextView.text = "Status: ${ complain.status }"

        applyExpandableLogic(holder.detailsTextView, "Plot Type: ${ complain.plotType }")
        applyExpandableLogic(holder.addressTextView, "Address: ${complain.houseNo}, ${complain.road}, ${complain.cityCorporationName}, ${complain.thanaName}")
        applyExpandableLogic(holder.detailsTextView, "Details: ${ complain.details }")

    }


    private fun applyExpandableLogic(textView: TextView, fullText: String) {
        val truncatedText = if (fullText.length > 25) fullText.substring(0, 25) + "..." else fullText
        textView.text = truncatedText
        var isExpanded = false

        textView.setOnClickListener {
            if (isExpanded) {
                // Collapse the text
                textView.text = truncatedText
                textView.maxLines = 1
                textView.ellipsize = TextUtils.TruncateAt.END
            } else {
                // Expand the text
                textView.text = fullText
                textView.maxLines = Integer.MAX_VALUE
                textView.ellipsize = null
            }
            isExpanded = !isExpanded
        }
    }

}