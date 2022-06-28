package pkg.erd;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;


class ERDModelTest {

    ERDModel erdModel = new ERDModel();

    @Test
    @Order(1)
    @DisplayName("Testing Generating ERD")
    void createERD() throws IOException {
        erdModel.generateERDiagram("dharmikdb");
    }
}