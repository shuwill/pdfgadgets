package org.spreadme.pdfgadgets.repository

import com.itextpdf.kernel.geom.Point
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.canvas.parser.EventType
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener
import org.spreadme.pdfgadgets.model.PageMetadata
import org.spreadme.pdfgadgets.model.Position
import org.spreadme.pdfgadgets.model.TextRenderInfo

class TextRenderEventListener(
    private val page: PageMetadata
) : IEventListener {

    val textRenderers = arrayListOf<TextRenderInfo>()

    override fun eventOccurred(data: IEventData?, type: EventType?) {
        if (type == EventType.RENDER_TEXT && data is com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo) {

            // determine bounding box
            val points: MutableList<Point> = ArrayList()
            val descentLine = data.descentLine
            points.add(Point(descentLine.startPoint.get(0).toDouble(), descentLine.startPoint.get(1).toDouble()))
            points.add(Point(descentLine.endPoint.get(0).toDouble(), descentLine.endPoint.get(1).toDouble()))

            val ascentLine = data.ascentLine
            points.add(Point(ascentLine.startPoint.get(0).toDouble(), ascentLine.startPoint.get(1).toDouble()))
            points.add(Point(ascentLine.endPoint.get(0).toDouble(), ascentLine.endPoint.get(1).toDouble()))

            val position = Position(page.index, page.pageSize, Rectangle.calculateBBox(points))
            if(data.text.isNotBlank()) {
                textRenderers.add(TextRenderInfo(data.text, position))
            }
        }
    }

    override fun getSupportedEvents(): MutableSet<EventType>? = null
}