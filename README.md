### 前言
一个拖动评价的View，分差、中、好三个等级。使用kotlin实现。三个评级分别用不同的背景和滑块显示
![](https://github.com/RommelLiang/EvaluationBar/blob/master/img/1566986196083.jpg)
![](https://github.com/RommelLiang/EvaluationBar/blob/master/img/gifdemo.gif)

### 实现方式
#### 绘制
将这个拖动条分了四块进行绘制
* 深灰色的边框和基础背景

```
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
```
* 不同颜色的拖动前景
```
//绘制进度颜色
    fun drawBckground(canvas: Canvas) {
        var rectf = RectF(2f, 1f, image_postion + image_weight / 2 - 1, m_height - 1)
        canvas.drawRoundRect(rectf, m_height / 2, m_height / 2, m_color_back)
    }
```
* 深灰色的分割线
```
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
```
* 滑块

```
 //绘制滑块
    fun drawImage(canvas: Canvas) {
        canvas.drawBitmap(slider, image_postion - image_weight / 2 - 2, 1f, Paint())
    }
```
#### 事件处理
通过在onTouchEvent的里面监听滑动距离而移动滑块和改变颜色
```
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
```

[完整代码](https://github.com/RommelLiang/EvaluationBar/blob/master/app/src/main/java/liang/cxx/rommel/evaluationbar/EvaluationProgressBar.kt)
