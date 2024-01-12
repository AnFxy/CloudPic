package com.fxyandtjh.voiceaccounting.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.fxyandtjh.voiceaccounting.R
import com.fxyandtjh.voiceaccounting.adapter.QuestionAdapter
import com.fxyandtjh.voiceaccounting.base.BaseFragment
import com.fxyandtjh.voiceaccounting.base.RxDialogSet
import com.fxyandtjh.voiceaccounting.base.setLimitClickListener
import com.fxyandtjh.voiceaccounting.databinding.FragAccountSecurityBinding
import com.fxyandtjh.voiceaccounting.local.LocalCache
import com.fxyandtjh.voiceaccounting.local.LocalCache.Companion.userInfo
import com.fxyandtjh.voiceaccounting.net.response.AccountSecurityMessage
import com.fxyandtjh.voiceaccounting.tool.timeStampToDate
import com.fxyandtjh.voiceaccounting.viewmodel.AccountSecurityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AccountSecurityFragment :
    BaseFragment<AccountSecurityViewModel, FragAccountSecurityBinding>() {

    private val viewModel: AccountSecurityViewModel by viewModels()

    private val questionAdapter: QuestionAdapter by lazy {
        val tempAdapter = QuestionAdapter {
            viewModel.updateSecurityId(it.id)
        }
        tempAdapter.setNewInstance(LocalCache.commonConfig.securityQuestions.toMutableList())
        tempAdapter
    }

    private val securityDialog: RxDialogSet? by lazy {
        context?.let {
            val tempDialog = RxDialogSet.provideDialog(it, R.layout.dia_security, false)
            tempDialog.setViewState<RecyclerView>(R.id.rv_question) {
                layoutManager = LinearLayoutManager(context)
                adapter = questionAdapter
            }.setViewState<TextView>(R.id.tv_cancel) {
                setLimitClickListener {
                    tempDialog.dismiss()
                }
            }
            val editAnswerText = tempDialog.getView<EditText>(R.id.et_answer)
            val btnConfirm = tempDialog.getView<TextView>(R.id.tv_confirm)
            editAnswerText.addTextChangedListener { editAble ->
                val currentText = editAble.toString()
                btnConfirm.isEnabled =
                    currentText.isNotEmpty() && viewModel._currentSelectID.value != -1
            }
            btnConfirm.setLimitClickListener {
                // Call API 修改密保问题
                viewModel.updateSecurityQuestion(editAnswerText.text.toString())
            }
            tempDialog
        }
    }

    override fun getViewMode(): AccountSecurityViewModel = viewModel

    override fun getViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): FragAccountSecurityBinding =
        FragAccountSecurityBinding.inflate(inflater, parent, false)

    override fun setLayout() {
        binding.btnChange.setLimitClickListener {
            // 重置选择的密保问题
            viewModel.updateSecurityId(viewModel._securityMessage.value.securityQuestionId)
            securityDialog?.show()
        }
    }

    override fun setObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._securityMessage.collect {
                updatePageData(it)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._currentSelectID.collect { currentId ->
                securityDialog?.setViewState<TextView>(R.id.tv_value) {
                    text = getQuestionText(currentId)
                }
                questionAdapter.selectID = currentId
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._modifySuccess.collect {
                ToastUtils.showShort(getText(R.string.modify_success))
                // 清除数据，关闭弹框
                securityDialog?.getView<EditText>(R.id.et_answer)?.setText("")
                securityDialog?.dismiss()
                viewModel.getUserAccountDetailFromRemote()
            }
        }
    }

    private fun updatePageData(accountSecurityMessage: AccountSecurityMessage) {
        val id = accountSecurityMessage.securityQuestionId
        val questionText = getQuestionText(id)
        // 页面密保问题文案
        binding.tvSecurityValue.text = questionText
        // 弹框密保数据
        securityDialog?.setViewState<TextView>(R.id.tv_value) {
            text = questionText
        }
        questionAdapter.selectID = id

        binding.livId.valueT = "${accountSecurityMessage.uid}"
        binding.livName.valueT = accountSecurityMessage.name
        binding.livPhoneNumber.valueT = accountSecurityMessage.phoneNumber
        binding.livCreateTime.valueT = timeStampToDate(userInfo.registerTime, detail = true)
    }

    private fun getQuestionText(currentID: Int) =
        if (currentID == -1 || LocalCache.commonConfig.securityQuestions.isEmpty()) {
            "---"
        } else {
            LocalCache.commonConfig.securityQuestions
                .find { it.id == currentID }?.question ?: "---"
        }
}