package water.fvec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.*;

import water.TestUtil;
import water.parser.DParseTask;

public class NewVectorTest extends TestUtil {
  static final double EPSILON = 1e-6;
  @BeforeClass public static void stall() { stall_till_cloudsize(1); }

  private void testImpl( long[] ls, int[] xs, Class C ) {
    NewChunk nv = new NewChunk(null,0);
    nv._ls = ls;
    nv._xs = xs;
    nv._len= ls.length;

    Chunk bv = nv.compress();
    // Compression returns the expected compressed-type:
    assertTrue( C.isInstance(bv) );
    // Also, we can decompress correctly
    for( int i=0; i<ls.length; i++ )
      assertEquals(ls[i]*DParseTask.pow10(xs[i]), bv.getd(i), bv.getd(i)*EPSILON);
  }

  // Test that various collections of parsed numbers compress as expected.
  @Test public void testCompression() {
    // A simple no-compress
    testImpl(new long[] {122, 3,44},
             new int [] {  0, 0, 0},
             C1Chunk.class);
    // Scaled-byte compression
    testImpl(new long[] {122,-3,44}, // 12.2, -3.0, 4.4 ==> 122e-1, -30e-1, 44e-1
             new int [] { -1, 0,-1},
             C1SChunk.class);
    // Positive-scale byte compression
    testImpl(new long[] {1000,200,30}, // 1000, 2000, 3000 ==> 1e3, 2e3, 3e3
             new int [] {   0,  1, 2},
             C1SChunk.class);
    // A simple no-compress short
    testImpl(new long[] {1000,200,32767, -32767,32},
             new int [] {   0,  1,    0,      0, 3},
             C2Chunk.class);
    // Scaled-byte compression
    testImpl(new long[] {50100,50101,50123,49999}, // 50100, 50101, 50123, 49999
             new int [] {    0,    0,    0,    0},
             C1SChunk.class);
    // Scaled-byte compression
    testImpl(new long[] {51000,50101,50123,49999}, // 51000, 50101, 50123, 49999
             new int [] {    0,    0,    0,    0},
             C2SChunk.class);
    // Scaled-short compression
    testImpl(new long[] {501000,501001,50123,49999}, // 50100.0, 50100.1, 50123, 49999
             new int [] {    -1,    -1,    0,    0},
             C2SChunk.class);
    // Integers
    testImpl(new long[] {123456,2345678,34567890},
             new int [] {     0,      0,       0},
             C4Chunk.class);
    // Floats
    testImpl(new long[] {1234,2345,314},
             new int [] {  -1,  -5, -2},
             C4FChunk.class);
    // Doubles
    testImpl(new long[] {1234,2345678,31415},
             new int [] {  40,     10,  -40},
             C8DChunk.class);
  }
}


