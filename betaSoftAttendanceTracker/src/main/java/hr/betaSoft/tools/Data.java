package hr.betaSoft.tools;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Data {

    private String ordinal;

    private String name;

    private String field;

    private String model;

    private String id;

    private String option;

    private String type;

    private String required;

    private String enabled;

    private List<String> items;

    private String multi;

    public static List<String> getColumnFieldsNotInDataList(Class<?> tempClass, List<Data> dataList) {
        List<String> columnFieldsNotInDataList = new ArrayList<>();
        Field[] fieldArray = tempClass.getDeclaredFields();

        for (Field field : fieldArray) {
            Annotation[] annotations = field.getDeclaredAnnotations();
            boolean isColumn = false;
            boolean isTemporal = false;
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().getSimpleName().equals("Column")) {
                    isColumn = true;
                }
                if (annotation.annotationType().getSimpleName().equals("Temporal")) {
                    isTemporal = true;
                }
            }
            if (isColumn || isTemporal) {
                boolean found = false;
                for (Data data : dataList) {
                    if (data.getField().equals(field.getName())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    columnFieldsNotInDataList.add(field.getName());
                }
            }
        }
        return columnFieldsNotInDataList;
    }
}