package nz.gen.geek_central.porter_duff_browser;
/*
    Demonstration of the effect of the various Porter-Duff transfer modes.
*/

import android.graphics.Bitmap;
import android.graphics.PorterDuff;

public class Main extends android.app.Activity
  {
    private android.graphics.Point ImageSize;
    private Bitmap SrcImage, DstImage;

    public static class ModeEntry
      {
        public final PorterDuff.Mode Mode;
        public final String Name, Formula;

        public ModeEntry
          (
            android.graphics.PorterDuff.Mode Mode,
            String Name,
            String Formula
          )
          {
            this.Mode = Mode;
            this.Name = Name;
            this.Formula = Formula;
          } /*ModeEntry*/
      } /*ModeEntry*/

    public static final ModeEntry[] Modes =
        {
          /* new ModeEntry(PorterDuff.Mode.ADD, "ADD", "Saturate(S + D)"), */ /* Honeycomb-only */
            new ModeEntry(PorterDuff.Mode.CLEAR, "CLEAR", "[0, 0]"),
            new ModeEntry(PorterDuff.Mode.DARKEN, "DARKEN", "[Sa + Da - Sa * Da,\n Sc * (1 - Da) + Dc * (1 - Sa)\n + min(Sc, Dc)]"),
            new ModeEntry(PorterDuff.Mode.DST, "DST", "[Da, Dc]"),
            new ModeEntry(PorterDuff.Mode.DST_ATOP, "DST_ATOP", "[Sa,\n Sa * Dc + Sc * (1 - Da)]"),
            new ModeEntry(PorterDuff.Mode.DST_IN, "DST_IN", "[Sa * Da, Sa * Dc]"),
            new ModeEntry(PorterDuff.Mode.DST_OUT, "DST_OUT", "[Da * (1 - Sa),\n Dc * (1 - Sa)]"),
            new ModeEntry(PorterDuff.Mode.DST_OVER, "DST_OVER", "[Sa + (1 - Sa) * Da,\n Rc = Dc + (1 - Da) * Sc]"),
            new ModeEntry(PorterDuff.Mode.LIGHTEN, "LIGHTEN", "[Sa + Da - Sa * Da,\n Sc * (1 - Da) + Dc * (1 - Sa)\n + max(Sc, Dc)]"),
            new ModeEntry(PorterDuff.Mode.MULTIPLY, "MULTIPLY", "[Sa * Da, Sc * Dc]"),
          /* new ModeEntry(PorterDuff.Mode.OVERLAY, "OVERLAY", "?"), */ /* Honeycomb-only */
            new ModeEntry(PorterDuff.Mode.SCREEN, "SCREEN", "[Sa + Da - Sa * Da,\n Sc + Dc - Sc * Dc]"),
            new ModeEntry(PorterDuff.Mode.SRC, "SRC", "[Sa, Sc]"),
            new ModeEntry(PorterDuff.Mode.SRC_ATOP, "SRC_ATOP", "[Da,\n Sc * Da + (1 - Sa) * Dc]"),
            new ModeEntry(PorterDuff.Mode.SRC_IN, "SRC_IN", "[Sa * Da, Sc * Da]"),
            new ModeEntry(PorterDuff.Mode.SRC_OUT, "SRC_OUT", "[Sa * (1 - Da),\n Sc * (1 - Da)]"),
            new ModeEntry(PorterDuff.Mode.SRC_OVER, "SRC_OVER", "[Sa + (1 - Sa) * Da,\n Rc = Sc + (1 - Sa) * Dc]"),
            new ModeEntry(PorterDuff.Mode.XOR, "XOR", "[Sa + Da - 2 * Sa * Da,\n Sc * (1 - Da) + (1 - Sa) * Dc]"),
        };

    private class ModeListAdapter extends android.widget.BaseAdapter
      {

        public class ModeItem
          {
            public final Bitmap Image;
            public final String Title;

            public ModeItem
              (
                ModeEntry Mode
              )
              {
                Image = Bitmap.createBitmap
                  (
                    /*width =*/ ImageSize.x,
                    /*height =*/ ImageSize.y,
                    /*config =*/ Bitmap.Config.ARGB_8888
                  );
                  {
                    final android.graphics.Canvas Draw = new android.graphics.Canvas(Image);
                    Draw.drawBitmap(DstImage, 0, 0, null);
                    final android.graphics.Paint Compose = new android.graphics.Paint();
                    Compose.setXfermode(new android.graphics.PorterDuffXfermode(Mode.Mode));
                    Draw.drawBitmap(SrcImage, 0, 0, Compose);
                    Image.prepareToDraw();
                  }
                Title = String.format("%s\n%s", Mode.Name, Mode.Formula);
              } /*ModeItem*/
          } /*ModeItem*/

        private ModeItem[] Items;

        public ModeListAdapter()
          {
            Items = new ModeItem[Modes.length];
            for (int i = 0; i < Modes.length; ++i)
              {
                Items[i] = new ModeItem(Modes[i]);
              } /*for*/
          } /*ModeListAdapter*/

        @Override
        public int getCount()
          {
            return
                Items.length;
          } /*getCount*/

        @Override
        public Object getItem
          (
            int Position
          )
          {
            return
                Items[Position];
          } /*getItem*/

        @Override
        public long getItemId
          (
            int Position
          )
          {
            return
                Position;
          } /*getItemId*/

        @Override
        public android.view.View getView
          (
            int Position,
            android.view.View TheView,
            android.view.ViewGroup ViewParent
          )
          {
            if (TheView == null)
              {
                TheView = getLayoutInflater().inflate(R.layout.image_view, null);
                TheView.setMinimumWidth(ImageSize.x * 5 / 4);
                TheView.setMinimumHeight(ImageSize.y * 3 / 2);
                  /* try to leave room for title text */
              } /*if*/
            final android.widget.ImageView TheImage =
                (android.widget.ImageView)TheView.findViewById(R.id.image_view);
            final android.widget.TextView TheTitle =
                (android.widget.TextView)TheView.findViewById(R.id.image_title);
            TheImage.setImageBitmap(Items[Position].Image);
            TheTitle.setText(Items[Position].Title);
            return
                TheView;
          } /*getView*/

      } /*ModeListAdapter*/

    @Override
    public void onCreate
      (
        android.os.Bundle SavedInstanceState
      )
      {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.main);
          {
            final int ThumbSize = (int)getResources().getDimension(R.dimen.thumbsize);
            System.err.printf("Thumbsize = %d*%d\n", ThumbSize, ThumbSize); /* debug */
            ImageSize = new android.graphics.Point(ThumbSize, ThumbSize);
          }
          {
            final int[] Pixels = new int[ImageSize.x * ImageSize.y];
            int dst = 0;
            for (int row = 0; row < ImageSize.y; ++row)
              {
                for (int col = 0; col < ImageSize.x; ++col)
                  {
                    Pixels[dst++] =
                            (ImageSize.y - row) * 255 / ImageSize.y << 24 /* alpha */
                        |
                            (ImageSize.x - col) * 255 / ImageSize.x << 16 /* red */
                        |
                            (ImageSize.x - col) * 255 / ImageSize.x << 8 /* green */
                        |
                            col * 255 / ImageSize.x /* blue */;
                  } /*for*/
              } /*for*/
            SrcImage = Bitmap.createBitmap(Pixels, ImageSize.x, ImageSize.y, Bitmap.Config.ARGB_8888);
          }
          {
            final int[] Pixels = new int[ImageSize.x * ImageSize.y];
            final int Color1 = 0xff0000;
            final int Color2 = 0x00ff00;
            int dst = 0;
            for (int row = 0; row < ImageSize.y; ++row)
              {
                for (int col = 0; col < ImageSize.x; ++col)
                  {
                    Pixels[dst++] =
                            (ImageSize.x - col) * 255 / ImageSize.x << 24 /* alpha */
                        |
                            (ImageSize.y - row) * 255 / ImageSize.x << 16 /* red */
                        |
                            row * 255 / ImageSize.y << 8 /* green */
                        |
                            row * 255 / ImageSize.y /* blue */;
                  } /*for*/
              } /*for*/
            DstImage = Bitmap.createBitmap(Pixels, ImageSize.x, ImageSize.y, Bitmap.Config.ARGB_8888);
          }
          {
            final int ThumbSize = (int)getResources().getDimension(R.dimen.smallthumbsize);
            ((android.widget.ImageView)findViewById(R.id.srcimage)).setImageBitmap
              (
                Bitmap.createScaledBitmap
                  (
                    /*src =*/ SrcImage,
                    /*width =*/ ThumbSize,
                    /*height =*/ ThumbSize,
                    /*filter =*/ true
                  )
              );
            ((android.widget.ImageView)findViewById(R.id.dstimage)).setImageBitmap
              (
                Bitmap.createScaledBitmap
                  (
                    /*src =*/ DstImage,
                    /*width =*/ ThumbSize,
                    /*height =*/ ThumbSize,
                    /*filter =*/ true
                  )
              );
          }
        ((android.widget.Gallery)findViewById(R.id.gallery))
            .setAdapter(new ModeListAdapter());
      } /*onCreate*/

  } /*Main*/
