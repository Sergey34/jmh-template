package seko.recursice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterConfig {
    List<FilterConfig> filterConfigs;
    String table;
}
