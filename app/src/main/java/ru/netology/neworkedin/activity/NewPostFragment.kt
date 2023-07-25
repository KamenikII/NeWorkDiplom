package ru.netology.neworkedin.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.neworkedin.R
import ru.netology.neworkedin.adapters.*
import ru.netology.neworkedin.utils.AndroidUtils.hideKeyboard
import ru.netology.neworkedin.utils.Companion.Companion.linkArg
import ru.netology.neworkedin.utils.Companion.Companion.mentionsCountArg
import ru.netology.neworkedin.utils.Companion.Companion.textArg
import ru.netology.neworkedin.utils.FloatValue.currentFragment
import ru.netology.neworkedin.utils.FloatValue.getExtensionFromUri
import ru.netology.neworkedin.utils.FloatValue.textNewPost
import ru.netology.neworkedin.databinding.FragmentNewPostBinding
import ru.netology.neworkedin.dataclass.*
import ru.netology.neworkedin.viewmodel.*
import java.io.File
import java.io.FileOutputStream

/** СОЗДАНИЕ ПОСТА */

@AndroidEntryPoint
class NewPostFragment : Fragment() {

    private val binding by lazy { FragmentNewPostBinding.inflate(layoutInflater) }
    private val viewModel: PostViewModel by activityViewModels()
    private val userViewModel: UserViewModel by viewModels()
    private var fragmentBinding: FragmentNewPostBinding? = null
    private var type: AttachmentType? = null
    private var attachRes: Attachment? = null
    private var mentionsIds:MutableList<Long> = mutableListOf()

    private var adapter = UsersAdapter(object : OnInteractionListenerUsers {
        override fun onTap(user: User) {
            mentionsIds.add(user.id)
            binding.countMentions.text = mentionsIds.size.toString()
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        fragmentBinding = binding

        with(binding) {

            arguments?.textArg?.let {
                edit.setText(it)
            }

            arguments?.linkArg?.let {
                inputLink.setText(it)
            }

            arguments?.mentionsCountArg?.let {
                countMentions.text = it.toString()
            }

            if (edit.text.isNullOrBlank()) {
                edit.setText(textNewPost)
            }

            edit.requestFocus()
            attachRes = viewModel.getEditedPostAttachment()
            Glide.with(photo)
                .load(attachRes?.url)
                .placeholder(
                    when (attachRes?.type) {
                        AttachmentType.AUDIO -> {
                            R.drawable.ic_baseline_audio_file_500
                        }
                        AttachmentType.VIDEO -> {
                            R.drawable.ic_baseline_video_library_500
                        }
                        else -> {
                            R.drawable.not_image_500
                        }
                    }
                )
                .timeout(10_000)
                .into(photo)


            if (!attachRes?.url.isNullOrBlank() && arguments?.textArg != null) {
                binding.photoContainer.visibility = View.VISIBLE
            }

            viewModel.media.observe(viewLifecycleOwner) {
                if (it.uri == null && attachRes?.url.isNullOrBlank()) {
                    binding.photoContainer.visibility = View.GONE
                    return@observe
                } else {
                    binding.photoContainer.visibility = View.VISIBLE
                    if (it.attachmentType == AttachmentType.IMAGE) {
                        binding.photo.setImageURI(it.uri)
                    }
                }
            }

            requireActivity().addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_new_post, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    when (menuItem.itemId) {
                        R.id.save -> {
                            fragmentBinding?.let {
                                viewModel.changeContent(
                                    it.edit.text.toString(),
                                    it.inputLink.text.toString().ifEmpty { null },
                                    mentionsIds
                                )
                                viewModel.save()
                                hideKeyboard(requireView())
                            }
                            true
                        }
                        else -> false
                    }

            }, viewLifecycleOwner)
            binding.listUsers.adapter = adapter
            clickListeners()

            return root
        }
    }

    override fun onStart() {
        currentFragment = javaClass.simpleName
        super.onStart()
    }

    @SuppressLint("IntentReset")
    private fun clickListeners() {

        binding.countMentions.setOnLongClickListener {
            mentionsIds = mutableListOf()
            binding.countMentions.text = mentionsIds.size.toString()
            true
        }

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Activity.RESULT_OK -> {
                        val uri: Uri? = it.data?.data
                        viewModel.changeMedia(uri, uri?.toFile(), AttachmentType.IMAGE)
                    }
                }
            }
        binding.pickPhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.CAMERA)
                .createIntent(pickPhotoLauncher::launch)
        }

        val pickMediaLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    Activity.RESULT_OK -> {
                        val uri: Uri? = it.data?.data
                        val contentResolver = context?.contentResolver
                        val inputStream = uri?.let { it1 -> contentResolver?.openInputStream(it1) }
                        val audioBytes = inputStream?.readBytes()

                        if (uri != null && contentResolver != null) {
                            val extension = getExtensionFromUri(uri, contentResolver)
                            val file = File(context?.getExternalFilesDir(null), "input.$extension")
                            FileOutputStream(file).use { outputStream ->
                                outputStream.write(audioBytes)
                                outputStream.flush()
                            }
                            viewModel.changeMedia(uri, file, type)
                        }
                    }
                    else -> {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.error_upload),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

        binding.uploadAudio.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
            intent.type = "audio/*"
            type = AttachmentType.AUDIO
            attachRes = attachRes?.copy(type = type!!)
            pickMediaLauncher.launch(intent)
        }

        binding.uploadVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            intent.type = "video/*"
            type = AttachmentType.VIDEO
            attachRes = attachRes?.copy(type = type!!)
            pickMediaLauncher.launch(intent)
        }


        binding.removePhoto.setOnClickListener {
            viewModel.deleteAttachment()
            attachRes = null
            viewModel.changeMedia(null, null, null)
        }


        binding.addMentions.setOnClickListener {
            binding.listUsers.isVisible = !binding.listUsers.isVisible
            lifecycleScope.launchWhenCreated {
                userViewModel.dataUsersList.collectLatest {
                    adapter.submitList(it)
                }
            }
        }


        with(binding) {

            viewModel.postCreated.observe(viewLifecycleOwner) {
                viewModel.loadPosts()
                findNavController().navigateUp()
            }

            fabCancel.setOnClickListener {
                if (viewModel.getEditedId() == 0L) {
                    textNewPost = edit.text.toString()
                } else {
                    edit.text?.clear()
                    viewModel.save()
                }
                hideKeyboard(root)
                findNavController().navigateUp()
            }

        }
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}