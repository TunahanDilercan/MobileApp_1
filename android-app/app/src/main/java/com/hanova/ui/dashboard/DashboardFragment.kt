package com.hanova.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hanova.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        // RecyclerView ayarları
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        chatAdapter = ChatAdapter(getDummyMessages())
        binding.chatRecyclerView.adapter = chatAdapter

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getDummyMessages(): List<Message> {
        return listOf(
            Message("Tunahan", "Merhaba!\nKullanabileceğim komutlar nelerdir ?"),
            Message("Akıllı Ev Asistanı", "Mevcut Komutlar ve İşlevleri\n" +
                    "Sıcaklık ve Nem\n" +
                    "\"Sıcaklık kaç derece?\" veya \"Nem oranı nedir?\"\n" +
                    "\"Sıcaklığı arttır\" veya \"Sıcaklığı azalt\"\n" +
                    "Gaz Sensörü (MQ2)\n" +
                    "\"Karbondioksit algılandı mı?\"\n" +
                    "Hareket Sensörü (PIR)\n" +
                    "\"Hareket algılandı mı?\" veya \"Ortamda bir hareket var mı?\"\n"+
                    "Piezo Sensörü\n" +
                    "\"Piezo durumu nedir?\" veya \"Titreşim algılandı mı?\"\n"
                    ),
        )

    }

}