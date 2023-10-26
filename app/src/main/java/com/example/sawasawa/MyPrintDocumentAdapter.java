package com.example.sawasawa;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import android.view.View;

import java.io.FileOutputStream;
import java.io.IOException;

public class MyPrintDocumentAdapter extends PrintDocumentAdapter {

    private Context context;
    private View contentView;

    public MyPrintDocumentAdapter(Context context, View contentView) {
        this.context = context;
        this.contentView = contentView;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
        // Create a new PDF document
        PrintedPdfDocument pdfDocument = new PrintedPdfDocument(context, newAttributes);

        // Get the printable area of the page
        int printableWidth = newAttributes.getMediaSize().getWidthMils() - 2 * newAttributes.getResolution().getHorizontalDpi();
        int printableHeight = newAttributes.getMediaSize().getHeightMils() - 2 * newAttributes.getResolution().getVerticalDpi();

        // Scale the content to fit within the printable area
        float scaleX = (float) contentView.getWidth() / (float) printableWidth;
        float scaleY = (float) contentView.getHeight() / (float) printableHeight;
        float scale = Math.min(scaleX, scaleY);

        // Calculate the horizontal and vertical offsets to center the content on the page
        int offsetX = (int) ((printableWidth - contentView.getWidth() / scale) / 2);
        int offsetY = (int) ((printableHeight - contentView.getHeight() / scale) / 2);

        // Iterate through each page of the document
        for (int i = 0; i < pdfDocument.getPages().size(); i++) {
            // Check if the print job has been cancelled
            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }

            // Start a new page in the PDF document
            PdfDocument.Page page = pdfDocument.startPage(i);

            // Center the content on the page
            Canvas canvas = page.getCanvas();
            canvas.translate(offsetX, offsetY);
            canvas.scale(scale, scale);

            // Draw the content on the page
            contentView.draw(canvas);

            // Finish the current page
            pdfDocument.finishPage(page);
        }

        // Notify the print framework of the layout result
        callback.onLayoutFinished(new PrintDocumentInfo.Builder("document.pdf").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build(), true);
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        // Create a new PDF document
        PdfDocument pdfDocument = new PdfDocument();

        // Iterate through each page of the document
        for (int i = 0; i < pages.length; i++) {
            // Check if the print job has been cancelled
            if (cancellationSignal.isCanceled()) {
                callback.onWriteCancelled();
                return;
            }
            // Start a new page in the PDF document
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(contentView.getWidth(), contentView.getHeight(), i).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            // Get the printable area of the page
            int printableWidth = page.getInfo().getPageWidth() - 2 * 72;
            int printableHeight = page.getInfo().getPageHeight() - 2 * 72;

            // Scale the content to fit within the printable area
            float scaleX = (float) contentView.getWidth() / (float) printableWidth;
            float scaleY = (float) contentView.getHeight() / (float) printableHeight;
            float scale = Math.min(scaleX, scaleY);

            // Calculate the horizontal and vertical offsets to center the content on the page
            int offsetX = (int) ((printableWidth - contentView.getWidth() / scale) / 2);
            int offsetY = (int) ((printableHeight - contentView.getHeight() / scale) / 2);

            // Center the content on the page
            Canvas canvas = page.getCanvas();
            canvas.translate(offsetX, offsetY);
            canvas.scale(scale, scale);

            // Draw the content on the page
            contentView.draw(canvas);

            // Finish the current page
            pdfDocument.finishPage(page);
        }

        // Write the PDF document to the output stream
        try {
            pdfDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        } finally {
            pdfDocument.close();
            pdfDocument = null;
        }

        // Notify the print framework that the write operation has completed
        callback.onWriteFinished(new PageRange[]{new PageRange(0, pages.length - 1)});
    }
}