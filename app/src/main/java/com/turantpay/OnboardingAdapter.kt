package com.turantpay

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView

data class OnboardingItem(
    val title: String,
    val description: String,
    val animationRes: Int
)

class OnboardingAdapter(private val items: List<OnboardingItem>) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    inner class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lottieAnimation: LottieAnimationView = view.findViewById(R.id.lottieAnimation)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_onboarding_page, parent, false)
        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        val item = items[position]
        holder.tvTitle.text = item.title
        holder.tvDescription.text = item.description
        // Fallback for animations (if no raw resource is provided)
        // holder.lottieAnimation.setAnimation(item.animationRes)
    }

    override fun getItemCount(): Int = items.size
}
