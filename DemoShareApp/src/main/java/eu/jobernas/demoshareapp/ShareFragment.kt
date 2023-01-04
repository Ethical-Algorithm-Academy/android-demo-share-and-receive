package eu.jobernas.demoshareapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import eu.jobernas.demoshareapp.databinding.FragmentShareBinding

/**
 * Share fragment
 *
 * @constructor Create empty Share fragment
 */
class ShareFragment : Fragment() {

    private var _binding: FragmentShareBinding? = null
    private var snackbar: Snackbar? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var shareViewModel: ShareViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shareViewModel = ViewModelProvider(this)[ShareViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _binding = FragmentShareBinding.inflate(inflater, container, false)
        binding.apply {
            shareViewModel.apply {
                onErrorThrowable.observe(viewLifecycleOwner) {
                    val error = it ?: return@observe
                    snackbar?.dismiss()
                    snackbar = Snackbar.make(shareContainer, "Error: ${error.message}, Cause: ${error.cause?.message}", Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.lb_dismiss) {
                            snackbar?.dismiss()
                        }
                    snackbar?.show()
                }
                shareWithNativeShareButton.setOnClickListener(this)
                shareWithClipBoardButton.setOnClickListener(this)
                shareWithUniversalLinkButton.setOnClickListener(this)
                shareWithNativeShareToTargetButton.setOnClickListener(this)
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        snackbar?.dismiss()
        snackbar = null
        _binding = null
        super.onDestroyView()
    }
}