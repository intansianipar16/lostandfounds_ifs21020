package com.ifs21020.lf.presentation.lf

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ifs21020.lf.data.model.DelcomObject
import com.ifs21020.lf.data.remote.MyResult
import com.ifs21020.lf.databinding.ActivityObjectManageBinding
import com.ifs21020.lf.helper.Utils.Companion.observeOnce
import com.ifs21020.lf.presentation.ViewModelFactory

class ObjectManageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityObjectManageBinding
    private val viewModel by viewModels<ObjectViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityObjectManageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupAction()
    }

    private fun setupView() {
        showLoading(false)
    }

    private fun setupAction() {
        val isAddObject = intent.getBooleanExtra(KEY_IS_ADD, true)
        if (isAddObject) {
            manageAddObject()
        } else {
            val delcomObject = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    intent.getParcelableExtra(KEY_OBJECT, DelcomObject::class.java)
                }
                else -> {
                    @Suppress("DEPRECATION")
                    intent.getParcelableExtra<DelcomObject>(KEY_OBJECT)
                }
            }
            if (delcomObject == null) {
                finishAfterTransition()
                return
            }
            manageEditObject(delcomObject)
        }
        binding.appbarObjectManage.setNavigationOnClickListener {
            finishAfterTransition()
        }
    }

    private fun manageAddObject() {
        binding.apply {
            appbarObjectManage.title = "Tambah Object"
            btnObjectManageSave.setOnClickListener {
                val title = etObjectManageTitle.text.toString()
                val description = etObjectManageDesc.text.toString()
                val status = if (radioButtonLost.isChecked) "lost" else "found"
                if (title.isEmpty() || description.isEmpty()) {
                    AlertDialog.Builder(this@ObjectManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage("Tidak boleh ada data yang kosong!")
                        setPositiveButton("Oke") { _, _ -> }
                        create()
                        show()
                    }
                    return@setOnClickListener
                }
                observePostObject(title, description, status)
            }
        }
    }

    private fun observePostObject(title: String, description: String, status: String) {
        viewModel.postObject(title, description, status).observeOnce { result ->
            when (result) {
                is MyResult.Loading -> {
                    showLoading(true)
                }
                is MyResult.Success -> {
                    showLoading(false)
                    val resultIntent = Intent()
                    setResult(RESULT_CODE, resultIntent)
                    finishAfterTransition()
                }
                is MyResult.Error -> {
                    AlertDialog.Builder(this@ObjectManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage(result.error)
                        setPositiveButton("Oke") { _, _ -> }
                        create()
                        show()
                    }
                    showLoading(false)
                }
            }
        }
    }

    private fun manageEditObject(lostfound: DelcomObject) {
        binding.apply {
            appbarObjectManage.title = "Ubah Object"
            etObjectManageTitle.setText(lostfound.title)
            etObjectManageDesc.setText(lostfound.description)
            btnObjectManageSave.setOnClickListener {
                val title = etObjectManageTitle.text.toString()
                val description = etObjectManageDesc.text.toString()
                if (title.isEmpty() || description.isEmpty()) {
                    AlertDialog.Builder(this@ObjectManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage("Tidak boleh ada data yang kosong!")
                        setPositiveButton("Oke") { _, _ -> }
                        create()
                        show()
                    }
                    return@setOnClickListener
                }
                observePutObject(lostfound.id, title, description, lostfound.isCompleted)
            }
        }
    }

    private fun observePutObject(
        lostfoundId: Int,
        title: String,
        description: String,
        isCompleted: Boolean,
    ) {
        viewModel.putObject(
            lostfoundId,
            title,
            description,
            isCompleted
        ).observeOnce { result ->
            when (result) {
                is MyResult.Loading -> {
                    showLoading(true)
                }
                is MyResult.Success -> {
                    showLoading(false)
                    val resultIntent = Intent()
                    setResult(RESULT_CODE, resultIntent)
                    finishAfterTransition()
                }
                is MyResult.Error -> {
                    AlertDialog.Builder(this@ObjectManageActivity).apply {
                        setTitle("Oh No!")
                        setMessage(result.error)
                        setPositiveButton("Oke") { _, _ -> }
                        create()
                        show()
                    }
                    showLoading(false)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbObjectManage.visibility =
            if (isLoading) View.VISIBLE else View.GONE
        binding.btnObjectManageSave.isActivated = !isLoading
        binding.btnObjectManageSave.text =
            if (isLoading) "" else "Simpan"
    }

    companion object {
        const val KEY_IS_ADD = "is_add"
        const val KEY_OBJECT = "object"
        const val RESULT_CODE = 1002
    }
}
