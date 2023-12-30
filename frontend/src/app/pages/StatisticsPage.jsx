import * as React from 'react';
import Grid from '@mui/material/Unstable_Grid2';
import MediaCard from '@/components/MediaCard';
import { Stack } from '@mui/material';

export default function StatisticsPage() {
  return (
    <Stack>
      <div>
        <Grid container rowSpacing={3} columnSpacing={3}>
          <Grid xs={4}>
            <MediaCard
              heading="CMYK"
              text="The CMYK color model (also known as process color, or four color) is a subtractive color model, based on the CMY color model, used in color printing, and is also used to describe the printing process itself."
            />
          </Grid>
          <Grid xs={4}>
            <MediaCard
              heading="HSL and HSV"
              text="HSL (for hue, saturation, lightness) and HSV (for hue, saturation, value; also known as HSB, for hue, saturation, brightness) are alternative representations of the RGB color model, designed in the 1970s by computer graphics researchers."
            />
          </Grid>
          <Grid xs={4}>
            <MediaCard
              heading="RGB"
              text="An RGB color space is any additive color space based on the RGB color model. RGB color spaces are commonly found describing the input signal to display devices such as television screens and computer monitors."
            />
          </Grid>
        </Grid>
      </div>
    </Stack>
  );
}
