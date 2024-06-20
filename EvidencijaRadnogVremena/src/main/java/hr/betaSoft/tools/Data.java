package hr.betaSoft.tools;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
}