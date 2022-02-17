package com.example.cs527_rdsmysql.ui.home

import ConnectionUtility.RDSConnection
import ConnectionUtility.ResultObject
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cs527_rdsmysql.R
import com.example.cs527_rdsmysql.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        // setting onClick listener for the
        root.findViewById<Button>(R.id.executeQueryButton).setOnClickListener { view ->
            if (view != null) {
                queryButtonOnClick(view)
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun queryButtonOnClick(View:View){
        val sqlIn:TextView = View.findViewById(R.id.sqlInTextView)
       // grabbing results based on sql text in the sqlInTextView
        val conn: RDSConnection = RDSConnection("jdbcURLhere", "userHere", "passHere")
        // try a connection here
        conn.connect()
        // if successful, we move onto sql query
        conn.executeSQL(sqlIn.text.toString())
        // if we executed the query successfully, we move onto results gathering
        // CHECK FOR NULL HERE
        val results: ResultObject? = conn.grabData(conn.sqlResultSet!!)
        // IF results is null, something went wrong with grabbing data
        // if we reached here we successfully grabbed our data
        print(results!!.intColumns.size.toString() + ", " + results.stringColumns.size.toString())
    }
}