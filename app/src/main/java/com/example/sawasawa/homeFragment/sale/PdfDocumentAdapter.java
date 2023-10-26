package com.example.sawasawa.homeFragment.sale;

import android.content.Context;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



public class PdfDocumentAdapter extends PrintDocumentAdapter {

    private Context mContext;
    private Uri mUri;
    private int mPageCount;

    public PdfDocumentAdapter(Context context, Uri uri) {
        mContext = context;
        mUri = uri;
        try {
            // Get the number of pages in the PDF document
            ParcelFileDescriptor parcelFileDescriptor = mContext.getContentResolver().openFileDescriptor(mUri, "r");
            PdfRenderer pdfRenderer = new PdfRenderer(parcelFileDescriptor);
            mPageCount = pdfRenderer.getPageCount();
            pdfRenderer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        // Create a new PrintDocumentInfo instance
        PrintDocumentInfo.Builder builder = new PrintDocumentInfo.Builder("sale_ticket.pdf");
        builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(mPageCount);

        // Set the PrintDocumentInfo and the layout result callback
        callback.onLayoutFinished(builder.build(), newAttributes != oldAttributes);
    }

    @Override
    public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        try {
            InputStream input = mContext.getContentResolver().openInputStream(mUri);
            OutputStream output = new FileOutputStream(destination.getFileDescriptor());

            // Copy the PDF document to the output stream
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }

            // Close the input and output streams
            input.close();
            output.close();

            // Signal the write result callback that the document was written successfully
            callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

        } catch (FileNotFoundException e) {
            // Signal the write result callback that the document could not be found
            callback.onWriteFailed("Document not found");
        } catch (Exception e) {
            // Signal the write result callback that an error occurred while writing the document
            e.printStackTrace();
            callback.onWriteFailed("Error printing document");
        }
    }
}