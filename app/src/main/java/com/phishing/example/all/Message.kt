package com.phishing.example.all

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.check_spam.*
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*

class Message : Fragment() {


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.check_spam, container,
                false)

        val classify_button = view.findViewById<Button>(R.id.classify_button);


        //invoke on button click
        classify_button.setOnClickListener( View.OnClickListener {
            val classifier = Classifier( requireContext() , "word_dict.json")
            //
            classifier.setMaxLength( 171 )
            //call the interface in classifier.kt
            classifier.setCallback( object : Classifier.DataCallback {
                override fun onDataProcessed( result :HashMap<String, Int>? ) {
                    val message = message_text.text.toString().toLowerCase().trim()
                    if ( !TextUtils.isEmpty( message ) ){
                        classifier.setVocab( result )
                        val tokenizedMessage = classifier.tokenize( message )
                        val paddedMessage = classifier.padSequence( tokenizedMessage )
                        val results = classifySequence( paddedMessage )
                        val class1 = results[1]
                        val res = 100 - ((36 - (class1*1000))*3.5);
                        val con = (35 - (class1*1000))*3.5;
                        if(res < 50){
                            result_text.setTextColor(resources.getColor(R.color.colorAccent));
                            result_text.text = "Spam: confidence $con%"
                        }else{
                            result_text.setTextColor(resources.getColor(R.color.green));
                            result_text.text = "Ham: confidence $res%"
                        }
                    }
                    else{
                        result_text.setTextColor(resources.getColor(R.color.colorAccent));
                        result_text.text = "empty text"

                    }

                }
            })
            classifier.loadData()
        })

        return view
    }

    //load the model
    @Throws(IOException::class)
    private fun loadModelFile(): MappedByteBuffer {
        val MODEL_ASSETS_PATH = "model.tflite"
        val assetFileDescriptor = activity!!.assets.openFd(MODEL_ASSETS_PATH)
        val fileInputStream = FileInputStream(assetFileDescriptor.getFileDescriptor())
        val fileChannel = fileInputStream.getChannel()
        val startoffset = assetFileDescriptor.getStartOffset()
        val declaredLength = assetFileDescriptor.getDeclaredLength()
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startoffset, declaredLength)
    }

    //classify the padded sequence got from classifier
    fun classifySequence ( sequence : IntArray ): FloatArray {
        val interpreter = Interpreter( loadModelFile() )
        val inputs : Array<FloatArray> = arrayOf( sequence.map{ it.toFloat() }.toFloatArray() )
        val outputs : Array<FloatArray> = arrayOf( floatArrayOf( 0.0f , 0.0f ) )
        interpreter.run( inputs , outputs )
        return outputs[0]
    }

}
