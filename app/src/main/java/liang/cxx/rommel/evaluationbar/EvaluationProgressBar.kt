package liang.cxx.rommel.evaluationbar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Created by Rommel on 2017/12/1.
 */

class EvaluationProgressBar : View {
    private var m_density: Float = 0.0f//dp
    private var img_height: Int = 30//滑块和进度条高度(默认三十)
    private var m_content: Context? = null
    //滑块样式
    private var slider: Bitmap? = null
    private var m_laughing: Bitmap? = null
    private var m_satisfactiong: Bitmap? = null
    private var m_sad: Bitmap? = null
    //滑块位置
    var image_postion: Float = 0.0f
    private //滑块宽度
    var image_weight: Float = 0.0f
    private //控件的宽和高
    var m_weight: Float = 0.0f
    private var m_height: Float = 0.0f
    private //每段的长度
    var m_cell: Float = 0.0f
    private //是否为第一次加载
    var is_first = true
    //画笔
    private var m_color_back: Paint? = null
    private var m_color_back_line: Paint? = null
    private var m_color_back_diver_one: Paint? = null
    private var m_color_back_diver_two: Paint? = null
    private var m_color_back_wall: Paint? = null
    private var back_line = "#C7C7C7"
    private var back_sad = "#CECECE"//差评背景
    private var back_satisfactiong = "#FFEAB0"//中评背景
    private var back_satisfactiong_line = "#FFD68C"//中评线框
    private var back_laughing = "#FFBBBB"//好评背景
    private var back_laughing_line = "#FF9F9F"//好评背景
    private var back_baseground = "#F8F8F8"//好评背景
    private var back_satisfactiong_diver = "#FFD68C"//中评分割线
    private var back_laughing_diver = "#FF9F9F"//好评分割线
    //触摸和滑动是否在滑块上
    private var is_touch_scope = false

    //滑动监听事件
    private var volChangeListener: VolChangeListener? = null
    private var vol = 2

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        m_density = context.resources.displayMetrics.density
        m_laughing = zoomImg(BitmapFactory.decodeResource(resources, R.mipmap.icon_laughing))
        m_satisfactiong = zoomImg(BitmapFactory.decodeResource(resources, R.mipmap.icon_satisfaction))
        m_sad = zoomImg(BitmapFactory.decodeResource(resources, R.mipmap.icon_sad))
        image_weight = m_laughing!!.width.toFloat()
        m_content = context
        m_height = (m_laughing!!.height + 1).toFloat()
        //画笔初始化
        m_color_back = Paint()
        m_color_back_line = Paint()
        m_color_back_line!!.style = Paint.Style.STROKE
        m_color_back_line!!.isAntiAlias = true
        m_color_back_line!!.strokeMiter = 1.0f
        m_color_back_diver_one = Paint()
        m_color_back_diver_two = Paint()
        m_color_back_diver_one!!.strokeMiter = 1.0F
        m_color_back_diver_two!!.strokeMiter = 1.0F
        m_color_back_wall = Paint()
        m_color_back_wall!!.color = Color.parseColor(back_line)
        m_color_back_wall!!.style = Paint.Style.STROKE
        m_color_back_wall!!.isAntiAlias = true
        m_color_back_wall!!.strokeWidth = 1.0f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawFilter = PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        image_postion = if (image_postion < image_weight / 2) image_weight / 2 else image_postion
        image_postion = if (image_postion > m_weight - image_weight / 2) m_weight - image_weight / 2 else image_postion
        if (image_postion <= m_cell) {
            //差评
            slider = m_sad
            m_color_back!!.color = Color.parseColor(back_sad)
            m_color_back_line!!.color = Color.parseColor(back_line)
        } else if (image_postion > m_cell && image_postion <= m_cell * 2) {
            //中评
            slider = m_satisfactiong
            m_color_back!!.color = Color.parseColor(back_satisfactiong)
            m_color_back_line!!.color = Color.parseColor(back_satisfactiong_line)
        } else if (image_postion > m_cell * 2) {
            //好评
            slider = m_laughing
            m_color_back!!.color = Color.parseColor(back_laughing)
            m_color_back_line!!.color = Color.parseColor(back_laughing_line)
        }
        drawWall(canvas)
        drawBckground(canvas)
        drawDiverLine(canvas)
        drawImage(canvas)
    }

    //绘制最外层线和整体背景
    fun drawWall(canvas: Canvas) {
        //外边框
        var rectf = RectF(2f, 0f, m_weight, m_height)
        canvas.drawRoundRect(rectf, m_height / 2, m_height / 2, m_color_back_wall)
        //基础背景
        var back_base = RectF(3f, 1f, m_weight - 1, m_height - 1)
        var back_base_color = Paint()
        back_base_color.color = Color.parseColor(back_baseground)
        canvas.drawRoundRect(back_base, m_height / 2, m_height / 2, back_base_color)
    }

    //绘制进度颜色
    fun drawBckground(canvas: Canvas) {
        var rectf = RectF(2f, 1f, image_postion + image_weight / 2 - 1, m_height - 1)
        canvas.drawRoundRect(rectf, m_height / 2, m_height / 2, m_color_back)
    }

    //根据滑块位置改变边框和分割线颜色
    fun drawDiverLine(canvas: Canvas) {
        //根据滑块位置改变外层边框颜色
        var rectF = RectF(2f, 0f, image_postion + image_weight / 2, m_height)
        canvas.drawRoundRect(rectF, m_height / 2, m_height / 2, m_color_back_line)
        if (image_postion <= m_cell) {
            //差评
            m_color_back_diver_one!!.color = Color.parseColor(back_line)
            canvas.drawLine(m_weight / 3, 0f, m_weight / 3, m_height, m_color_back_diver_one)
            m_color_back_diver_two!!.color = Color.parseColor(back_line)
            canvas.drawLine(m_weight * 2 / 3, 0f, m_weight * 2 / 3, m_height, m_color_back_diver_two)
        } else if (image_postion > m_cell && image_postion <= m_cell * 2) {
            //中评
            m_color_back_diver_one!!.color = Color.parseColor(back_satisfactiong_diver)
            canvas.drawLine(m_weight / 3, 0f, m_weight / 3, m_height, m_color_back_diver_one)
            m_color_back_diver_two!!.color = Color.parseColor(back_line)
            canvas.drawLine(m_weight * 2 / 3, 0f, m_weight * 2 / 3, m_height, m_color_back_diver_two)
        } else if (image_postion > m_cell * 2) {
            //好评
            m_color_back_diver_one!!.color = Color.parseColor(back_laughing_diver)
            canvas.drawLine(m_weight / 3, 0f, m_weight / 3, m_height, m_color_back_diver_one)
            m_color_back_diver_two!!.color = Color.parseColor(back_laughing_diver)
            canvas.drawLine(m_weight * 2 / 3, 0f, m_weight * 2 / 3, m_height, m_color_back_diver_two)
        }
    }

    //绘制滑块
    fun drawImage(canvas: Canvas) {
        canvas.drawBitmap(slider, image_postion - image_weight / 2 - 2, 1f, Paint())
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        m_weight = (width - 2).toFloat()
        m_cell = m_weight / 3
        if (is_first) {
            //首次加载默认值
            image_postion = m_weight * 2 / 3
            is_first = false
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = getSize((200 * m_density).toInt(), widthMeasureSpec)
        var height = getSize(m_laughing!!.width + 1, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    private fun getSize(defaultSize: Int, measureSpec: Int): Int {
        var m_size = defaultSize
        var mode = View.MeasureSpec.getMode(measureSpec)
        var size = MeasureSpec.getSize(measureSpec)
        when (mode) {
            MeasureSpec.AT_MOST -> {
                //当前尺寸是当前View能取的最大尺寸-wrap_content
                m_size = defaultSize
            }
            MeasureSpec.EXACTLY -> {
                //当前的尺寸就是当前View应该取的尺寸-match_parent
                m_size = size
            }
            MeasureSpec.UNSPECIFIED -> {
                //父容器没有对当前View有任何限制，当前View可以任意取尺寸-固定尺寸（如100dp
                m_size = size
            }
        }
        return m_size
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var x_postion = event.x
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (x_postion > image_postion - image_weight / 2
                        && x < image_postion + image_weight / 2) {
                    //触摸事件在滑块上
                    image_postion = x_postion
                    is_touch_scope = true
                } else {
                    //触摸事件不在滑块上
                    image_postion = x_postion
                    setImgPostion()
                    invalidate()
                    onChange()
                    return false
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (is_touch_scope) {
                    image_postion = x_postion
                    invalidate()
                    onChange()
                }
            }
            MotionEvent.ACTION_UP -> {
                if (is_touch_scope) {
                    is_touch_scope = false
                    setImgPostion()
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                if (is_touch_scope) {
                    is_touch_scope = false
                    setImgPostion()
                }
            }
        }
        return true
    }

    private fun setImgPostion() {
        //松手后滑块走到指定的位置
        if (image_postion <= m_cell) {
            //差评
            image_postion = m_cell
        } else if (image_postion > m_cell && image_postion <= m_cell * 2) {
            //中评
            image_postion = m_cell * 2
        } else if (image_postion > m_cell * 2) {
            //好评
            image_postion = m_cell * 3
        }
        invalidate()
        onChange()
    }

    //滑块位置变化监听
    fun onChange() {
        if (volChangeListener != null) {
            if (image_postion <= m_cell) {
                vol = 1
            } else if (image_postion > m_cell && image_postion <= 2 * m_cell) {
                vol = 2
            } else if (image_postion > 2 * m_cell) {
                vol = 3
            }
            volChangeListener!!.onVolChange(vol)
        }
    }

    private fun zoomImg(btm: Bitmap): Bitmap {
        //获取图片宽高
        var width = btm.width
        var height = btm.height
        //计算缩放比例
        var scaleWidth = (img_height * m_density) / width
        var scaleHeight = (img_height * m_density) / height
        //获取Matrix参数
        var matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        //返回新的图片
        return Bitmap.createBitmap(btm, 0, 0, width, height, matrix, true)
    }

    interface VolChangeListener {
        fun onVolChange(vol: Int)
    }

    fun volVhangeListener(volChangeListener :VolChangeListener) {
        this.volChangeListener = volChangeListener
    }
}
