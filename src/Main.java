package nz.gen.geek_central.porter_duff_browser;
/*
    Demonstration of the effect of the various Porter-Duff transfer modes.

    Copyright 2011, 2012 by Lawrence D'Oliveiro <ldo@geek-central.gen.nz>.

    Licensed under the Apache License, Version 2.0 (the "License"); you may not
    use this file except in compliance with the License. You may obtain a copy of
    the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
    License for the specific language governing permissions and limitations under
    the License.
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
            new ModeEntry(PorterDuff.Mode.CLEAR, "CLEAR", "[0, 0]"),
            new ModeEntry(PorterDuff.Mode.DARKEN, "DARKEN", "[Sa + Da - Sa * Da,\n Sc * (1 - Da) + Dc * (1 - Sa)\n + min(Sc, Dc)]"),
            new ModeEntry(PorterDuff.Mode.DST, "DST", "[Da, Dc]"),
            new ModeEntry(PorterDuff.Mode.DST_ATOP, "DST_ATOP", "[Sa,\n Sa * Dc + Sc * (1 - Da)]"),
            new ModeEntry(PorterDuff.Mode.DST_IN, "DST_IN", "[Sa * Da, Sa * Dc]"),
            new ModeEntry(PorterDuff.Mode.DST_OUT, "DST_OUT", "[Da * (1 - Sa),\n Dc * (1 - Sa)]"),
            new ModeEntry(PorterDuff.Mode.DST_OVER, "DST_OVER", "[Sa + (1 - Sa) * Da,\n Rc = Dc + (1 - Da) * Sc]"),
            new ModeEntry(PorterDuff.Mode.LIGHTEN, "LIGHTEN", "[Sa + Da - Sa * Da,\n Sc * (1 - Da) + Dc * (1 - Sa)\n + max(Sc, Dc)]"),
            new ModeEntry(PorterDuff.Mode.MULTIPLY, "MULTIPLY", "[Sa * Da, Sc * Dc]"),
            new ModeEntry(PorterDuff.Mode.SCREEN, "SCREEN", "[Sa + Da - Sa * Da,\n Sc + Dc - Sc * Dc]"),
            new ModeEntry(PorterDuff.Mode.SRC, "SRC", "[Sa, Sc]"),
            new ModeEntry(PorterDuff.Mode.SRC_ATOP, "SRC_ATOP", "[Da,\n Sc * Da + (1 - Sa) * Dc]"),
            new ModeEntry(PorterDuff.Mode.SRC_IN, "SRC_IN", "[Sa * Da, Sc * Da]"),
            new ModeEntry(PorterDuff.Mode.SRC_OUT, "SRC_OUT", "[Sa * (1 - Da),\n Sc * (1 - Da)]"),
            new ModeEntry(PorterDuff.Mode.SRC_OVER, "SRC_OVER", "[Sa + (1 - Sa) * Da,\n Rc = Sc + (1 - Sa) * Dc]"),
            new ModeEntry(PorterDuff.Mode.XOR, "XOR", "[Sa + Da - 2 * Sa * Da,\n Sc * (1 - Da) + (1 - Sa) * Dc]"),
        };

    static class ModesExtra
      /* extra modes only available in Honeycomb and later */
      {
        public static final ModeEntry[] Modes =
            {
                new ModeEntry(PorterDuff.Mode.ADD, "ADD", "Saturate(S + D)"), /* Honeycomb-only */
                new ModeEntry(PorterDuff.Mode.OVERLAY, "OVERLAY", "?"), /* Honeycomb-only */
            };
      } /*ModesExtra*/

    static int Color
      (
      /* all in range [0.0 .. 1.0] */
        float Alpha,
        float R,
        float G,
        float B
      )
      /* constructs a colour value from the specified components. */
      {
      /* Note components have pre-multiplied alpha, since this is
        how the Porter-Duff modes are defined */
        return
                (int)(Alpha * 255) << 24
            |
                (int)(R * Alpha * 255) << 16
            |
                (int)(G * Alpha * 255) << 8
            |
                (int)(B * Alpha * 255);
      } /*Color*/

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
            final java.util.TreeSet<ModeEntry> SortedModes = new java.util.TreeSet<ModeEntry>
              (
                new java.util.Comparator<ModeEntry>()
                  {
                    @Override
                    public int compare
                      (
                        ModeEntry Mode1,
                        ModeEntry Mode2
                      )
                      {
                        return
                            Mode1.Name.compareTo(Mode2.Name);
                      } /*compare*/
                  } /*Comparator*/
              );
            for (int i = 0; i < Modes.length; ++i)
              {
                SortedModes.add(Modes[i]);
              }
            try
              {
                final ModeEntry[] ExtraModes = ModesExtra.Modes;
                for (int i = 0; i < ExtraModes.length; ++i)
                  {
                    SortedModes.add(ExtraModes[i]);
                  }
              }
            catch (NoClassDefFoundError TooOld)
              {
              }
            catch (ExceptionInInitializerError TooOld)
              {
              } /*catch*/
              {
                Items = new ModeItem[SortedModes.size()];
                int i = 0;
                for (ModeEntry Mode : SortedModes)
                  {
                    Items[i++] = new ModeItem(Mode);
                  } /*for*/
              }
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
          /* set higher-quality window pixel format to reduce banding */
            final android.view.Window Window = getWindow();
            final android.view.WindowManager.LayoutParams LayoutParams = Window.getAttributes();
            System.err.printf("PorterDuff.Main: initial window pixel format = %d\n", LayoutParams.format); /* debug */
            LayoutParams.format = android.graphics.PixelFormat.RGBA_8888; /* not default pre-Gingerbread */
            Window.setAttributes(LayoutParams);
          }
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
                        Color
                          (
                            /*Alpha =*/ (float)(ImageSize.y - row) / ImageSize.y,
                            /*R =*/ (float)(ImageSize.x - col) / ImageSize.x,
                            /*G =*/ (float)(ImageSize.x - col) / ImageSize.x,
                            /*B =*/ (float)col / ImageSize.x
                          );
                  } /*for*/
              } /*for*/
            SrcImage = Bitmap.createBitmap(Pixels, ImageSize.x, ImageSize.y, Bitmap.Config.ARGB_8888);
          }
          {
            final int[] Pixels = new int[ImageSize.x * ImageSize.y];
            int dst = 0;
            for (int row = 0; row < ImageSize.y; ++row)
              {
                for (int col = 0; col < ImageSize.x; ++col)
                  {
                    Pixels[dst++] =
                        Color
                          (
                            /*Alpha =*/ (float)(ImageSize.x - col) / ImageSize.x,
                            /*R =*/ (float)(ImageSize.y - row) / ImageSize.x,
                            /*G =*/ (float)row / ImageSize.y,
                            /*B =*/ (float)row / ImageSize.y
                          );
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
