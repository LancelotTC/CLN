//package com.example.cln;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Point;
//
//import com.google.android.gms.maps.MapView;
//
//class MapOverlay extends com.google.android.maps.Overlay {
//    public boolean draw(Canvas canvas, MapView mapView,
//                        boolean shadow, long when, Context context)
//    {
//        super.draw(canvas, mapView, shadow);
//
//        //---translate the GeoPoint to screen pixels---
//        Point screenPts = new Point();
//        mapView.getProjection().toPixels(p, screenPts);
//
//        //---add the marker---
//        Bitmap bmp = BitmapFactory.decodeResource(
//                context.getResources(), R.drawable.pink);
//        canvas.drawBitmap(bmp, screenPts.x, screenPts.y-32, null);
//        return true;
//    }
//}
