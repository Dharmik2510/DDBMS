package pkg.export;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class ExportSAVModelTest {

  ExportSAVModel exportSAVModel = new ExportSAVModel();

    @Test
    @Order(1)
    @DisplayName("Testing export structure and values ")
    public void exportWithValues() throws IOException
    {
        exportSAVModel.exportWithValues("dharmikdb");
    }

  @Test
  @Order(2)
  @DisplayName("Testing export structure without values ")
  public void exportWithoutValues() throws IOException
  {
    exportSAVModel.exportWithOutValues("dharmikdb");
  }
}