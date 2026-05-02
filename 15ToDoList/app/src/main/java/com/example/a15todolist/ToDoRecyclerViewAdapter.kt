package com.example.a15todolist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.a15todolist.databinding.ItemTodoBinding
import com.example.a15todolist.db.ToDoEntity

// 뷰홀더 클래스를 가지는 어댑터를 상속한 어댑터 클래스
class ToDoRecyclerViewAdapter(private val toDoList: ArrayList<ToDoEntity>)
    : RecyclerView.Adapter<ToDoRecyclerViewAdapter.MyViewHolder>() {

        // 내부 클래스로 구현한 뷰홀더 클래스
        inner class MyViewHolder(binding: ItemTodoBinding)
            : RecyclerView.ViewHolder(binding.root) {
                val tv_item_importance = binding.tvItemImportance
            val tv_item_title = binding.tvItemTitle
            val root = binding.root
            }

    // 뷰홀더 객체 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        // ToDoItem을 생성하는 뷰홀더 객체 생성
        val binding: ItemTodoBinding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MyViewHolder(binding)
    }

    // 받은 데이터를 생성한 뷰홀더 객체에 넣어주는 방법을 결정
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val toDoData = toDoList[position]

        val color: Int = when(toDoData.importance) {
            1 -> {
                R.color.red
            }

            2 -> {
                R.color.yellow
            }

            3 -> {
                R.color.green
            }

            else -> {}
        } as Int

        // 배경 색깔 설정
        holder.tv_item_importance.setBackgroundResource(color)

        // 항목 하나하나 설정
        holder.tv_item_importance.text = toDoData.importance.toString()
        holder.tv_item_title.text = toDoData.title
    }

    // 데이터가 총 몇 개인지 변환
    override fun getItemCount(): Int {
        return toDoList.size
    }
}