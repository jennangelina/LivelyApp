package com.example.proyek_android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyek_android.R
import com.example.proyek_android.journal
import com.example.proyek_android.task

import android.graphics.drawable.Drawable




class adapterJournal (
    private val listJournal: ArrayList<journal>): RecyclerView.Adapter<adapterJournal.ListViewHolder> () {
        inner class ListViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
            var _tvjournalTitle: TextView = itemView.findViewById<TextView>(R.id.tvJournalTitleHist)
            var _tvjournalContent: TextView = itemView.findViewById<TextView>(R.id.tvJournalContentHist)
            var _ivJournalMood: ImageView = itemView.findViewById<ImageView>(R.id.ivJournalMood)
            var _tvJournalDayHist: TextView = itemView.findViewById(R.id.tvJournalDayHist)
            var _tvJournalMonthHist: TextView = itemView.findViewById(R.id.tvJournalMonthHist)
            var _tvJournalYearHist: TextView = itemView.findViewById(R.id.tvJournalYearHist)
            var _cvJournalItem: CardView = itemView.findViewById(R.id.cvJournalItem)
        }

    private lateinit var onItemClickCallback: adapterJournal.OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: journal)
    }

    fun setOnItemClickCallback(onItemClickCallback: adapterJournal.OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.itemjournal, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var data = listJournal[position]

        holder._tvjournalTitle.setText(data.journal_title)
        holder._tvjournalContent.setText(data.journal_content)
        holder._tvJournalDayHist.setText(data.journal_day)
        holder._tvJournalYearHist.setText(data.journal_year)

        when(data.journal_month) {
            "1" -> holder._tvJournalMonthHist.setText("Jan")
            "2" -> holder._tvJournalMonthHist.setText("Feb")
            "3" -> holder._tvJournalMonthHist.setText("Mar")
            "4" -> holder._tvJournalMonthHist.setText("Apr")
            "5" -> holder._tvJournalMonthHist.setText("May")
            "6" -> holder._tvJournalMonthHist.setText("Jun")
            "7" -> holder._tvJournalMonthHist.setText("Jul")
            "8" -> holder._tvJournalMonthHist.setText("Aug")
            "9" -> holder._tvJournalMonthHist.setText("Sep")
            "10" -> holder._tvJournalMonthHist.setText("Oct")
            "11" -> holder._tvJournalMonthHist.setText("Nov")
            "12" -> holder._tvJournalMonthHist.setText("Dec")
        }

        when(data.journal_mood) {
            "happy" -> holder._ivJournalMood.setImageResource(R.drawable.mood_happy)
            "smile" -> holder._ivJournalMood.setImageResource(R.drawable.mood_smile)
            "sad" -> holder._ivJournalMood.setImageResource(R.drawable.mood_sad)
            "cry" -> holder._ivJournalMood.setImageResource(R.drawable.mood_cry)
            "angry" -> holder._ivJournalMood.setImageResource(R.drawable.mood_angry)
        }

        holder._cvJournalItem.setOnClickListener {
            onItemClickCallback.onItemClicked(data)
        }
    }

    override fun getItemCount(): Int {
        return listJournal.count()
    }


}