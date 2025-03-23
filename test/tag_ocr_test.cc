#include <tesseract/baseapi.h>
#include <leptonica/allheaders.h>

int main()
{

    tesseract::TessBaseAPI api;
    // // Initialize tesseract-ocr with English, without specifying tessdata path
    if (api.Init(NULL, "eng")) {
        fprintf(stderr, "Could not initialize tesseract.\n");
        exit(1);
    }

    // Open input image with leptonica library

    // Pix *image = pixRead("images/DE011400445141.jpg");
    Pix *image = pixRead("images/1736692524294_2.jpg");
    api.SetImage(image);
    // // Get OCR result
    char *outText;
    outText = api.GetUTF8Text();
    printf("OCR output:\n%s", outText);
    delete [] outText;

    // // Destroy used object and release memory
    pixDestroy(&image);

    return 0;
}
