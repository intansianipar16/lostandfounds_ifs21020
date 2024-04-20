package com.ifs21020.lf.presentation.lf

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ifs21020.lf.data.model.DelcomObject
import com.ifs21020.lf.data.remote.MyResult
import com.ifs21020.lf.data.remote.response.LostFoundObjectResponse
import com.ifs21020.lf.databinding.ActivityObjectDetailBinding
import com.ifs21020.lf.helper.Utils.Companion.observeOnce
import com.ifs21020.lf.presentation.ViewModelFactory

class ObjectDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityObjectDetailBinding
    private val viewModel by viewModels<ObjectViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var isChanged: Boolean = false
    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == ObjectManageActivity.RESULT_CODE) {
            recreate()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityObjectDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupAction()
    }
    private fun setupView() {
        showComponent(false)
        showLoading(false)
    }
    private fun setupAction() {
        val lostfoundId = intent.getIntExtra(KEY_OBJECT_ID, 0)
        if (lostfoundId == 0) {
            finish()
            return
        }
        observeGetObject(lostfoundId)
        binding.appbarObjectDetail.setNavigationOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra(KEY_IS_CHANGED, isChanged)
            setResult(RESULT_CODE, resultIntent)
            finishAfterTransition()
        }
    }
    private fun loadObject(lostfound: LostFoundObjectResponse) {
        showComponent(true)
        binding.apply {
            tvObjectDetailTitle.text = lostfound.title
            tvObjectDetailDate.text = "Dibuat pada: ${lostfound.createdAt}"
            tvObjectDetailDesc.text = lostfound.description
            cbObjectDetailIsCompleted.isChecked = lostfound.isCompleted == 1
            cbObjectDetailIsCompleted.setOnCheckedChangeListener { _, isChecked ->
                viewModel.putObject(
                    lostfound.id,
                    lostfound.title,
                    lostfound.description,
                    isChecked
                ).observeOnce {
                    when (it) {
                        is MyResult.Error -> {
                            if (isChecked) {
                                Toast.makeText(
                                    this@ObjectDetailActivity,
                                    "Gagal menyelesaikan object: " + lostfound.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@ObjectDetailActivity,
                                    "Gagal batal menyelesaikan object: " + lostfound.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        is MyResult.Success -> {
                            if (isChecked) {
                                Toast.makeText(
                                    this@ObjectDetailActivity,
                                    "Berhasil menyelesaikan object: " + lostfound.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@ObjectDetailActivity,
                                    "Berhasil batal menyelesaikan object: " + lostfound.title,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            if ((lostfound.isCompleted == 1) != isChecked) {
                                isChanged = true
                            }
                        }
                        else -> {}
                    }
                }
            }
            ivObjectDetailActionDelete.setOnClickListener {
                val builder = AlertDialog.Builder(this@ObjectDetailActivity)
                builder.setTitle("Konfirmasi Hapus Object")
                    .setMessage("Anda yakin ingin menghapus object ini?")
                builder.setPositiveButton("Ya") { _, _ ->
                    observeDeleteObject(lostfound.id)
                }
                builder.setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss() // Menutup dialog
                }
                val dialog = builder.create()
                dialog.show()
            }
            ivObjectDetailActionEdit.setOnClickListener {
                val delcomObject = DelcomObject(
                    lostfound.id,
                    lostfound.title,
                    lostfound.description,
                    lostfound.isCompleted == 1,
                    lostfound.cover
                )
                val intent = Intent(
                    this@ObjectDetailActivity,
                    ObjectManageActivity::class.java
                )
                intent.putExtra(ObjectManageActivity.KEY_IS_ADD, false)
                intent.putExtra(ObjectManageActivity.KEY_OBJECT, delcomObject)
                launcher.launch(intent)
            }
        }
    }
    private fun observeGetObject(lostfoundId: Int) {
        viewModel.getObject(lostfoundId).observeOnce { result ->
            when (result) {
                is MyResult.Loading -> {
                    showLoading(true)
                }
                is MyResult.Success -> {
                    showLoading(false)
                    loadObject(result.data.data.lostFound)
                }
                is MyResult.Error -> {
                    Toast.makeText(
                        this@ObjectDetailActivity,
                        result.error,
                        Toast.LENGTH_SHORT
                    ).show()
                    showLoading(false)
                    finishAfterTransition()
                }
            }
        }
    }
    private fun observeDeleteObject(lostfoundId: Int) {
        showComponent(false)
        showLoading(true)
        viewModel.deleteObject(lostfoundId).observeOnce {
            when (it) {
                is MyResult.Error -> {
                    showComponent(true)
                    showLoading(false)
                    Toast.makeText(
                        this@ObjectDetailActivity,
                        "Gagal menghapus object: ${it.error}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is MyResult.Success -> {
                    showLoading(false)
                    Toast.makeText(
                        this@ObjectDetailActivity,
                        "Berhasil menghapus object",
                        Toast.LENGTH_SHORT
                    ).show()
                    val resultIntent = Intent()
                    resultIntent.putExtra(KEY_IS_CHANGED, true)
                    setResult(RESULT_CODE, resultIntent)
                    finishAfterTransition()
                }
                else -> {}
            }
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.pbObjectDetail.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showComponent(status: Boolean) {
        binding.llObjectDetail.visibility =
            if (status) View.VISIBLE else View.GONE
    }
    companion object {
        const val KEY_OBJECT_ID = "object_id"
        const val KEY_IS_CHANGED = "is_changed"
        const val RESULT_CODE = 1001
    }
}