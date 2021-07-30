package com.example.minesweeper.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.minesweeper.MainActivity;

import java.util.concurrent.ThreadLocalRandom;

public class CustomView extends View {
    private Rect main_rect;
    private Rect s_rect;
    private Paint main_paint,s_paint,e_paint,no_paint;
    int x,y,bomb_no= MainActivity.bn,hb=0;
    Vibrator vibrator;
    int arr[]= new int[65];
    int arr_x[]= new int[65];
    int arr_y[]= new int[65];
    int arr_bomb[]= new int[65];
    int score=0,hscore=0,gameover=0;
    @RequiresApi(api = Build.VERSION_CODES.M)
    public CustomView(Context context) {
        super(context);
        init(null);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void init(@Nullable AttributeSet set)
    {
        main_rect =new Rect();
        s_rect =new Rect();
        no_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        no_paint.setColor(Color.BLACK);
        s_paint =new Paint((Paint.ANTI_ALIAS_FLAG));
        e_paint =new Paint((Paint.ANTI_ALIAS_FLAG));
        main_paint =new Paint(Paint.ANTI_ALIAS_FLAG);
        main_paint.setColor(Color.BLACK);
        e_paint.setColor(Color.LTGRAY);
        s_paint.setColor(Color.BLUE);
        array();
        assign_bomb();
        if(set==null)
        {
            return;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        x=getWidth();
        y=getHeight();
        main_rect.left=10;
        main_rect.top=y/4;
        main_rect.right=main_rect.left+x-20;
        main_rect.bottom=main_rect.top+x-10;
        canvas.drawRect(main_rect,main_paint);
        int z=x/8-3;
        no_paint.setTextSize(z-x/18);
        no_paint.setColor(Color.BLACK);
        SharedPreferences getscore = getContext().getSharedPreferences("Hscore",Context.MODE_PRIVATE);
        hscore = getscore.getInt("Hscore",0);
        if(score>hscore)
            hscore=score;
        canvas.drawText("Score:"+score,x/3+x/15,2*(z-x/18),no_paint);
        no_paint.setColor(Color.BLUE);
        canvas.drawText("Highest Score:"+hscore,x/3-x/15,3*(z-x/18),no_paint);
        no_paint.setColor(Color.RED);
        canvas.drawText("Hidden Bomb:"+hb,x/3-x/15,4*(z-x/18),no_paint);
        //int w=y/4-3;
        for(int k=y/4+5,l=1;l<=64;k+=z) {
            for (int i = 15, j = 1; j <= 8; i += z, j++) {
                s_rect.left = i;
                s_rect.top = k;
                s_rect.right = s_rect.left + x / 8 - 10;
                s_rect.bottom = s_rect.top + x / 8 - 10;
               if(arr[l]==1) {
                    canvas.drawRect(s_rect, s_paint);
               }
               if(arr[l]==0)
               {
                   int xy;
                   if(arr_bomb[l]==3)
                   {
                       vibrator.vibrate(300);
                       e_paint.setColor(Color.RED);
                       canvas.drawRect(s_rect,e_paint);
                       gameover=1;
                   }
                   else {
                       e_paint.setColor(Color.LTGRAY);
                       xy = count_bomb(l);
                       canvas.drawRect(s_rect,e_paint);
                       canvas.drawText(" "+xy,i,k+z-x/24,no_paint);
                   }
               }
               if(gameover==1)
               {
                    SharedPreferences.Editor editor= getscore.edit();
                    editor.putInt("Hscore",hscore);
                    editor.apply();
                   no_paint.setColor(Color.RED);
                   canvas.drawText("Game Over!",x/3,5*(z-x/18),no_paint);
               }
               arr_x[l]=i;
               arr_y[l]=k;
                l++;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean value= super.onTouchEvent(event);
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                float tx= event.getX();
                float ty = event.getY();
                if(gameover==0) {
                    for (int i = 1; i <= 64; i++) {
                        if (tx >= arr_x[i] && tx <= (arr_x[i] + x / 8 - 10) && ty >= arr_y[i] && ty <= (arr_y[i] + x / 8 - 10)) {
                            if (arr[i] == 1 && arr_bomb[i] != 3)
                                score++;
                            arr[i] = 0;
                        }
                    }
                    postInvalidate();
                }
                return true;
            }
            case MotionEvent.ACTION_MOVE:{
            return true;
            }
        }
        return value;
    }
    public void array()
    {
        for(int i=1;i<=64;i++)
            arr[i]=1;
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void assign_bomb()
    {
        for(int i=0;i<bomb_no;i++)
        {
           int u= ThreadLocalRandom.current().nextInt(1,64);
           arr_bomb[u]=3;
        }
        for(int i=1;i<65;i++)
        {
            if(arr_bomb[i]==3)
            {
                hb++;
            }
        }

    }

int  count_bomb(int location)
{
    int count=0;
    if(location>9&&array_search(location)==0) {
        if (arr_bomb[location - 9] == 3)
            count++;
    }
    if(location>8) {
        if (arr_bomb[location - 8] == 3)
            count++;
    }
    if(location>7&&array_searchh(location)==0) {
        if (arr_bomb[location - 7] == 3) {
            count++;
        }
    }
    if(location>1&&array_search(location)==0)
    {
        if (arr_bomb[location - 1] == 3)
            count++;
    }
    if(location<64&&array_searchh(location)==0) {
        if (arr_bomb[location + 1] == 3)
            count++;
    }
    if(location<58&&array_search(location)==0)
    if(arr_bomb[location+7]==3)
        count++;
    if(location<57)
    {
        if (arr_bomb[location + 8] == 3)
            count++;
    }
    if(location<56&&array_searchh(location)==0)
    {
        if (arr_bomb[location + 9] == 3)
            count++;
    }
return count;
}
int array_search(int location)
{
    int temp=0;
    for(int i=9,j=8;i<=57;i+=8,j+=8)
    {
        if(location==i) {
            temp = 1;
            break;
        }
    }
    return temp;
}
    int array_searchh(int location)
    {
        int temp=0;
        for(int j=8;j<=64;j+=8)
        {
            if(location==j) {
                temp = 1;
                break;
            }
        }
        return temp;
    }
}
