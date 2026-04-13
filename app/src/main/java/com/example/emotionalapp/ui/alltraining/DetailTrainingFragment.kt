package com.example.emotionalapp.ui.alltraining

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.emotionalapp.data.TrainingMenuItem
import com.example.emotionalapp.data.TrainingMenuType

class TrainingDetailFragment : Fragment() {

    companion object {
        private const val ARG_MENU_ID = "arg_menu_id"
        private const val ARG_MENU_TITLE = "arg_menu_title"
        private const val ARG_MENU_TYPE = "arg_menu_type"

        fun newInstance(item: TrainingMenuItem): TrainingDetailFragment {
            return TrainingDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MENU_ID, item.id)
                    putString(ARG_MENU_TITLE, item.title)
                    putString(ARG_MENU_TYPE, item.type.name)
                }
            }
        }
    }

    private val viewModel: TrainingDetailViewModel by viewModels()

    private val menuId: String by lazy {
        arguments?.getString(ARG_MENU_ID).orEmpty()
    }

    private val menuTitle: String by lazy {
        arguments?.getString(ARG_MENU_TITLE).orEmpty()
    }

    private val menuType: TrainingMenuType by lazy {
        arguments?.getString(ARG_MENU_TYPE)?.let { TrainingMenuType.valueOf(it) }
            ?: TrainingMenuType.INTRO
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.load(menuType, menuTitle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val uiState by viewModel.uiState.collectAsState()

                TrainingDetailScreen(
                    pageTitle = uiState.pageTitle,
                    selectedTab = uiState.selectedTab,
                    recordItems = uiState.recordItems,
                    trainingItems = uiState.trainingItems,
                    onBackClick = {
                        parentFragmentManager.popBackStack()
                    },
                    onRecordTabClick = {
                        viewModel.onRecordTabClick(menuType)
                    },
                    onTrainingTabClick = {
                        viewModel.onTrainingTabClick()
                    },
                    onDetailItemClick = { clickedItem ->
                        if (clickedItem.currentProgress == "잠김") {
                            Toast.makeText(requireContext(), "잠금 상태입니다.", Toast.LENGTH_SHORT).show()
                            return@TrainingDetailScreen
                        }

                        if (
                            clickedItem.progressDenominator == clickedItem.progressNumerator &&
                            clickedItem.currentProgress != "GO" &&
                            clickedItem.currentProgress != "보기"
                        ) {
                            Toast.makeText(requireContext(), "모두 완료한 훈련입니다.", Toast.LENGTH_SHORT).show()
                            return@TrainingDetailScreen
                        }

                        clickedItem.targetActivityClass?.let { targetClass ->
                            val intent = Intent(requireContext(), targetClass).apply {
                                putExtra("TRAINING_ID", clickedItem.id)
                                putExtra("TRAINING_TITLE", clickedItem.title)

                                if (clickedItem.currentProgress == "보기") {
                                    putExtra("reportDateMillis", clickedItem.reportDateMillis ?: -1L)
                                }
                            }
                            startActivity(intent)
                        } ?: run {
                            Toast.makeText(
                                requireContext(),
                                "${clickedItem.title}: 상세 페이지 준비 중입니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            }
        }
    }
}