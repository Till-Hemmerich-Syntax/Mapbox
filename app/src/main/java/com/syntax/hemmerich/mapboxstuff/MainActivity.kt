package com.syntax.hemmerich.mapboxstuff

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.mapbox.geojson.GeoJson
import com.mapbox.geojson.Geometry
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PolygonAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PolygonAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPolygonAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMoveListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.syntax.hemmerich.mapboxstuff.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var annotationApi : AnnotationPlugin
    private lateinit var circleAnnotationManager : CircleAnnotationManager
    private lateinit var polygonAnnotationManager : PolygonAnnotationManager

    private var points = MutableLiveData(mutableListOf<Point>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        annotationApi = binding.mapView.annotations
        circleAnnotationManager = annotationApi.createCircleAnnotationManager()
        polygonAnnotationManager = annotationApi.createPolygonAnnotationManager()

        binding.fab.setOnClickListener {
            var center = binding.mapView.mapboxMap.cameraState.center
            binding.txtLat.text = center.latitude().toString()
            binding.txtLong.text = center.longitude().toString()
            val newPoint = Point.fromLngLat(center.longitude(),center.latitude())
            var newList = points.value?.plus(newPoint)

            points.postValue(newList as MutableList<Point>?)
            //Toast.makeText(this, points.value?.first()?.longitude().toString(),Toast.LENGTH_LONG).show()
            createMapMaker(newPoint)

        }


        points.observe(this, Observer {
            if(it.isEmpty()) return@Observer
            Toast.makeText(this, it.last().latitude().toString(),Toast.LENGTH_SHORT).show()
            if(it.size>=3){
                createPolygon(listOf(it))
            }
        })



    }
    fun createPolygon(points : List<List<Point>>){
        val polygonAnnotationOptions: PolygonAnnotationOptions = PolygonAnnotationOptions()
            .withPoints(points)
            // Style the polygon that will be added to the map.
            .withFillColor("#ee4e8b")
            .withFillOpacity(0.4)
// Add the resulting polygon to the map.

        polygonAnnotationManager?.create(polygonAnnotationOptions)
    }

    fun createMapMaker(point: Point){
        val circleAnnotationOptions: CircleAnnotationOptions = CircleAnnotationOptions()
            // Define a geographic coordinate.
            .withPoint(point)
            // Style the circle that will be added to the map.
            .withCircleRadius(8.0)
            .withCircleColor("#ee4e8b")
            .withCircleStrokeWidth(2.0)
            .withCircleStrokeColor("#ffffff")
        // Add the resulting circle to the map.
        circleAnnotationManager.create(circleAnnotationOptions)
    }


}