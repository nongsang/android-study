package com.example.a03fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class BlueFragment: Fragment() {

    // Fragment 클래스의 onCreateView() 재정의
    override fun onCreateView(
        inflater: LayoutInflater,   // 뷰를 생성하는 객체
        container: ViewGroup?,      // 생성할 뷰(자식 뷰)가 들어갈 부모 뷰
        savedInstanceState: Bundle? // 이전 프래그먼트 객체에서 전달된 데이터(번들)
    ): View? {
        // 뷰를 생성하는 객체에 프래그먼트의 레이아웃을 지정한다.
        // fragment_blue 레이아웃으로 설정
        // 자식 뷰가 들어갈 부모 뷰는 container,
        // 지금 즉시 부모 뷰에 자식 뷰를 추가하지 않을 것이므로 false
        return inflater.inflate(R.layout.fragment_blue, container, false)
    }
}