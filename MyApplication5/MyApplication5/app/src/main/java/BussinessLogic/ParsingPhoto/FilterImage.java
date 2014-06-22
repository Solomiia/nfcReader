package BussinessLogic.ParsingPhoto;

import android.graphics.Bitmap;
import android.util.Log;

import Models.RectangleParameters;

/**
 * Created by Solomiia on 4/10/2014.
 */
public class FilterImage {

    public RectangleParameters FilterImageAlgorithm(Bitmap bmp){

        int x1=0, x2=0, y1=0, y2=0;
        for (int i = 1; i < bmp.getHeight();i++ )
        {
            for (int j = 1; j < bmp.getWidth();j++ )
            {
                if( bmp.getPixel(j,i)<7  &&  bmp.getPixel(j - 1, i)>240  && bmp.getPixel(j, i - 1)>240 ){

                    for (int k = j; k < bmp.getWidth() - 1;k++ )
                    {

                        if ((bmp.getPixel(k, i) < 7) && (bmp.getPixel(k + 1, i) > 240) && (k-j>30)) {
                            int count1 = 0;

                            for (int g = j; g < k;g++ ){
                                if(bmp.getPixel(g, i)<7){
                                    count1++;
                                }
                            }//get total width

                            if(count1==k-j){
                                x1 = j;
                                y1 = i;
                                x2 = k;
                            }
                        }
                    }
                    for (int a = i; a < bmp.getHeight() - 1;a++ )
                    {
                        if ((bmp.getPixel(j, a) < 7) && (bmp.getPixel(j, a + 1) > 240) && (a- i > 30)) {

                            int count2 = 0;

                            for (int x = i; x < a;x++ )
                            {
                                if(bmp.getPixel(j, x)<7){
                                    count2++;
                                }
                            }


                            if (count2 == (a - i))
                            {

                                y2 = a;
                            }
                            else {
                                Log.d("","check");
                            }
                        }

                    }

                    if ((bmp.getPixel(x2, y2) < 7) && (bmp.getPixel(x2 + 1, y2) > 240) && (bmp.getPixel(x2, y2 + 1) > 240))
                    {

                        boolean r1 = false;
                        boolean r2 = false;
                        int count3 = 0;
                        for (int y = y1; y < y2;y++ )
                        {
                            if(bmp.getPixel(x2, y)<7){
                                count3++;
                            }
                        }

                        if (count3== y2 - y1) {
                            r1 = true;
                        }
                        if(r1==true){
                            int count4=0;
                            for (int x = x1; x < x2;x++ )
                            {
                                if(bmp.getPixel(x, y1)<7){
                                    count4++;
                                }
                            }

                            if(count4==x2-x1){
                                r2 = true;
                                Log.d("","values :  X1 " + x1 + "   y1 :" + y1 + "   width : " + (x2 - x1) + "  height :  " + (y2 - y1));
                            }
                        }
                    }

                }

            }// initial point loop
        }// first if
        RectangleParameters parameters = new RectangleParameters();
        parameters.RX1 = x1;
        parameters.RX2 = x2;
        parameters.RY1 = y1;
        parameters.RY2 = y2;
        return parameters;
    }
}
