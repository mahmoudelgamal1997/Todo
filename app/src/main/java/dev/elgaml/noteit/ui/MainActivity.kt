package dev.elgaml.noteit.ui

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar
import dev.elgaml.noteit.R
import dev.elgaml.noteit.adapter.INoteRVAdapter
import dev.elgaml.noteit.adapter.NoteRVAdapter
import dev.elgaml.noteit.data.Note
import dev.elgaml.noteit.databinding.ActivityMainBinding
import dev.elgaml.noteit.helper.SharedHelper
import dev.elgaml.noteit.helper.TargetPrompt


class MainActivity : AppCompatActivity(), INoteRVAdapter {

    private var binding: ActivityMainBinding? = null
    lateinit var viewModel: NoteViewModel
    lateinit var noteAdapter: NoteRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        binding?.fabAddNote?.setOnClickListener {
            //opening addNote activity on click on fab button
            val addNoteIntent = Intent(this@MainActivity, AddNote::class.java)
            startActivity(addNoteIntent)
        }

        //initializing recyclerView
        binding?.recyclerView?.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        if (SharedHelper.getBoolean(this,SharedHelper.PROMPT) != true){
            var list=ArrayList<TargetPrompt.PromptType>()
            list.add(TargetPrompt.PromptType(getString(R.string.add_note),getString(R.string.add_description), binding?.fabAddNote!!))

            var targetPrompt=TargetPrompt(this,list)
            targetPrompt.showTapTarget()
        }
        //initializing adapter
        noteAdapter = NoteRVAdapter(this, this)

        //passing adapter to recyclerView
        binding?.recyclerView?.adapter = noteAdapter

        //setting swipe to delete item
        setUpSwipeToDeleteItem()

        //creating a instance or object of viewModel
        viewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        viewModel.allNotes.observe(this, Observer {
            Log.d("TAGYOYO", "$it")
            binding?.textEmpty!!.isVisible = it.isEmpty()
            noteAdapter.submitList(it)
        })
    }

    private fun setUpSwipeToDeleteItem() {
        val swipeToDelete = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                //this will be empty as on up and down movement we do not need to do any thing with note
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //getting position of item which is swiped
                val itemPosition = viewHolder.adapterPosition
                //getting currentList
                val currentList =  noteAdapter.currentList.toMutableList()
                //getting the swiped item
                val swipedItem = currentList[itemPosition]
                //removing item from list
                currentList.removeAt(itemPosition)

                //removing from database
                viewModel.removeNote(swipedItem)

                //updating recycler view
                noteAdapter.submitList(currentList)

                val snackbar = Snackbar.make(binding?.root!!, "Note removed", Snackbar.LENGTH_LONG)
                snackbar.setAction("UNDO") {
                    val newCurrentList  = noteAdapter.currentList.toMutableList()
                    newCurrentList.add(itemPosition, swipedItem)

                    //adding item back to database
                    viewModel.addNote(swipedItem)
                    //updating recycler view
                    noteAdapter.submitList(newCurrentList)
                }
                snackbar.setActionTextColor(Color.YELLOW)
                snackbar.show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDelete)
        itemTouchHelper.attachToRecyclerView(binding?.recyclerView)
    }

    private fun runRecyclerViewAnimation(recyclerView: RecyclerView) {
        val context = recyclerView.context
        val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.recyclerview_animation)
        recyclerView.layoutAnimation = controller
        recyclerView.scheduleLayoutAnimation()
    }

    override fun onResume() {
        super.onResume()
        runRecyclerViewAnimation(binding?.recyclerView!!)
    }

    override fun onCardClicked(note: Note) {
        //whenever a card of note is clicked we navigate to openNote activity passing the note
        val openNoteIntent = Intent(this, OpenNote::class.java)
        openNoteIntent.putExtra("SelectedNote", note)
        startActivity(openNoteIntent)
    }

    override fun onBackPressed() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Exit")
        builder.setMessage("Are You Sure?")
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
            finish()
        })
        builder.setNegativeButton("No",
            DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
        val alert: AlertDialog = builder.create()
        alert.show()
    }
}
