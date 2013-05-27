package net.yoojia.imagemap;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import net.yoojia.imagemap.support.Shape;
import net.yoojia.imagemap.support.ShapeExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HighlightImageView基于TouchImageView的功能，在ImageView的Canvas上绘制一些形状。
 */
public class HighlightImageView extends TouchImageView implements ShapeExtension {

	public HighlightImageView(Context context) {
		this(context,null);
	}

	public HighlightImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	private Map<Object,Shape> shapesCache = new HashMap<Object, Shape>();
	
	private int savedShapesCount = 0;

    private OnShapeActionListener onShapeClickListener;

    public void setOnShapeClickListener(OnShapeActionListener onShapeClickListener){
        this.onShapeClickListener = onShapeClickListener;
    }

    @Override
	public void addShape(Shape shape){
		shapesCache.put(shape.tag, shape);
		postInvalidate();
	}

    @Override
	public void addShapes(List<Shape> shapes){
		for(Shape shape : shapes){
			shapesCache.put(shape.tag, shape);
		}
		postInvalidate();
	}
	
    @Override
	public void removeShape(Object tag){
		if(shapesCache.containsKey(tag)){
			shapesCache.remove(tag);
			postInvalidate();
		}
	}

    public List<Shape> getShapes(){
        return new ArrayList<Shape>(shapesCache.values());
    }

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
        canvas.save();
        for(Shape shape : shapesCache.values()){
            shape.onDraw(canvas);
        }
        onDrawWithCanvas(canvas);
        canvas.restore();
	}

    /**
     * 如果继承HighlightImageView，并需要在Canvas上绘制，可以Override这个方法来实现。
     * @param canvas 画布
     */
    protected void onDrawWithCanvas(Canvas canvas){}

    @Override
    protected void onClick(float xOnView, float yOnView) {
        if(onShapeClickListener == null) return;
        for(Shape shape : shapesCache.values()){
            if(shape.inArea(xOnView,yOnView)){
                // 如果一个形状被点击，通过监听接口回调给点击事件的关注者。
                onShapeClickListener.onShapeClick(shape, xOnView, yOnView);
                break; // 只有一个形状可以被点击
            }
        }
    }

    @Override
	protected void postScale(float scaleFactor, float scaleCenterX,float scaleCenterY) {
		super.postScale(scaleFactor, scaleCenterX, scaleCenterY);
		if(scaleFactor != 0){
            for(Shape shape : shapesCache.values()){
                if(scaleFactor != 0){
                    shape.onScale(scaleFactor, scaleCenterX, scaleCenterY);
                }
            }
		}
	}

    @Override
    protected void postTranslate(float deltaX, float deltaY) {
        super.postTranslate(deltaX, deltaY);
        if( !(deltaX == 0 && deltaY == 0)){
            for(Shape shape : shapesCache.values()){
                shape.onTranslate(deltaX, deltaY);
            }
        }
    }

}
