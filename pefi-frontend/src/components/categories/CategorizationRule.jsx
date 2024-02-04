import {TreeItem} from "@mui/x-tree-view";
import Grid from "@mui/material/Unstable_Grid2";
import {Checkbox, FormControlLabel, IconButton, MenuItem, Select, TextField} from "@mui/material";
import Typography from "@mui/material/Typography";
import utils from "@/utils.js";
import {ArrowDownward, ArrowUpward, PriceChange, Close as DeleteIcon} from "@mui/icons-material";
import {DatePicker} from "@mui/x-date-pickers";
import {useState} from "react";
import dayjs from "dayjs";

export default function CategorizationRule({ ruleData, fields, ruleTypes, onDelete, updateRuleData, depth: depth = 0 }) {

    const [rule, setRule] = useState(ruleData);

    function selectRuleTypeOrField(value) {
        let field = fields.find(f => f.field === value);

        if (field) {
            rule.ruleBasedOn = {
                field: value,
                type: field.type
            };

            rule.rule = undefined;
            rule.left = undefined;
            rule.right = undefined;
        } else {
            switch (value) {
                case "AND":
                case "OR":
                    rule.left = {
                        id: utils.generateUUID()
                    };
                    rule.right = {
                        id: utils.generateUUID()
                    };
                    break;
                case "NOT":
                    rule.left = undefined;

                    rule.right = {
                        id: utils.generateUUID()
                    };
                    break;
            }

            rule.rule = {
                rule: value,
                applicableType: undefined
            };

            rule.ruleBasedOn = undefined;
        }

        updateRule();
    }

    function updateRule() {
        setRule({...rule});
        updateRuleData(rule);
    }

    function fieldsAndLogicalOperators() {
        return fields.map(f => { return { name: f.field, type: f.type } })
            .concat(
                ruleTypes.filter(rt => rt.applicableType === null || rt.applicableType === undefined)
                    .map(rt => {
                        return {
                            name: rt.rule,
                            type: rt.applicableType
                        }
                    })
            );
    }

    function ruleTypeName(ruleType) {
        switch (ruleType) {
            case "STRING_REGEX": return "matches";
            case "STRING_CONTAINS": return "contains";
            case "BOOLEAN_EQ":
            case "NUMERIC_EQUALS":
            case "STRING_EQ": return "equals";
            case "STRING_IS_EMPTY": return "is empty";
            case "TIMESTAMP_GREATER_THAN": return "is later than";
            case "NUMERIC_GREATER_THAN": return "is greater than";
            case "TIMESTAMP_LESS_THAN": return "is earlier than";
            case "NUMERIC_LESS_THAN": return "is less than";
            case "TIMESTAMP_BETWEEN":
            case "NUMERIC_BETWEEN": return "is between";
            case "AND": return "And";
            case "OR": return "Or";
            case "NOT": return "Not";
            default: return ruleType.toString();
        }
    }

    function renderCategorization() {
        return <TreeItem
            key={`${rule.id}`}
            sx={{
                pt: 1,
                pb: 1
            }}
            nodeId={`${rule.id}`}
            label={
                <Grid container spacing={1}>
                    <Grid xs={1} lg={1}>
                        <Select
                            sx={{ width: "100%" }}
                            defaultValue={"placeholder"}
                            value={rule.ruleBasedOn?.field ?? rule.rule?.rule ?? "placeholder"}
                            onChange={(e) => selectRuleTypeOrField(e.target.value)}
                        >
                            <MenuItem disabled value="placeholder">
                                <Typography sx={{ color: 'gray' }}>Field/Rule</Typography>
                            </MenuItem>
                            {
                                fieldsAndLogicalOperators().map(item => (
                                    <MenuItem
                                        key={`${rule.id}-${item.name}-${depth}`}
                                        value={item.name}
                                    >
                                        { utils.toPascalCase(item.name.replace(/_/g, " ")) }
                                    </MenuItem>
                                ))
                            }
                        </Select>
                    </Grid>

                    {
                        rule.ruleBasedOn?.type &&
                        <Grid xs={1} lg={1}>
                            <Select
                                sx={{ width: "100%" }}
                                defaultValue={"placeholder"}
                                value={rule.rule?.rule ?? "placeholder"}
                                onChange={(e) => {
                                    rule.rule = ruleTypes.find(rt => rt.rule === e.target.value);
                                    updateRule();
                                }}
                            >
                                <MenuItem disabled value="placeholder">
                                    <Typography sx={{ color: 'gray' }}>Rule</Typography>
                                </MenuItem>
                                {
                                    ruleTypes.filter(rt => rt.applicableType === rule.ruleBasedOn.type)
                                        .map(rt => {
                                            return (
                                                <MenuItem
                                                    key={`${rule.id}-${rt.rule}-${depth}`}
                                                    value={rt.rule}
                                                >
                                                    { ruleTypeName(rt.rule) }
                                                </MenuItem>
                                            )
                                        })
                                }
                            </Select>
                        </Grid>
                    }

                    {renderRuleOptions()}

                    {
                        depth === 0 &&
                        <Grid xs={1} lg={1}>
                            <IconButton
                                sx={{ height: "100%" }}
                                onClick={onDelete}
                            >
                                <DeleteIcon />
                            </IconButton>
                        </Grid>
                    }
                </Grid>
            }
        >
            {
                rule.left &&
                <CategorizationRule
                    ruleData={rule.left}
                    fields={fields}
                    ruleTypes={ruleTypes}
                    depth={depth + 1}
                    updateRuleData={(ruleData) => {
                        rule.left = ruleData;
                        updateRule();
                    }}
                />
            }
            {
                rule.right &&
                <CategorizationRule
                    ruleData={rule.right}
                    fields={fields}
                    ruleTypes={ruleTypes}
                    depth={depth + 1}
                    updateRuleData={(ruleData) => {
                        rule.right = ruleData;
                        updateRule();
                    }}
                />
            }
        </TreeItem>
    }

    function renderRuleOptions() {
        switch (rule.rule?.rule) {
            case "STRING_REGEX":
            case "STRING_EQ":
            case "STRING_CONTAINS": return (
                <Grid xs={1} lg={1}>
                    <TextField
                        sx={{ width: "100%" }}
                        label={"Value"}
                        value={rule.stringValue ?? ""}
                        onChange={(e) => {
                            rule.stringValue = e.target.value;
                            updateRule();
                        }}
                    />
                </Grid>
            );
            case "STRING_IS_EMPTY": return ("");
            case "BOOLEAN_EQ": return (
                <Grid xs={1} lg={1}>
                    <Checkbox
                        sx={{ width: "100%", height: "100%"}}
                        checked={rule.booleanValue ?? false}
                        icon={
                            <>
                                <PriceChange style={{ color: '#d44' }} />
                                <ArrowDownward style={{ color: '#d44' }} />
                            </>
                        }
                        checkedIcon={
                            <>
                                <PriceChange style={{ color: '#4d4' }} />
                                <ArrowUpward style={{ color: '#4d4' }} />
                            </>
                        }
                        onChange={(e) => {
                            rule.booleanValue = e.target.checked;
                            updateRule();
                        }}
                    />
                </Grid>
            );
            case "NUMERIC_EQUALS": return (
                <Grid xs={1} lg={1}>
                    <TextField
                        sx={{ width: "100%" }}
                        label={"Value"}
                        type="number"
                        value={rule.numericValue ?? 0}
                        onChange={(e) => {
                            rule.numericValue = e.target.value;
                            updateRule();
                        }}
                    />
                </Grid>
            )
            case "TIMESTAMP_GREATER_THAN": return (
                <Grid xs={1} lg={1}>
                    <DatePicker
                        sx={{ width: "100% "}}
                        label="Value"
                        value={dayjs(rule.timestampGreaterThan) ?? ""}
                        onChange={(newValue) => {
                            rule.timestampGreaterThan = newValue;
                            updateRule();
                        }}
                    />
                </Grid>
            )
            case "NUMERIC_GREATER_THAN": return (
                <Grid xs={1} lg={1}>
                    <TextField
                        sx={{ width: "100%" }}
                        label={"Value"}
                        type="number"
                        value={rule.numericGreaterThan ?? 0}
                        onChange={(e) => {
                            rule.numericGreaterThan = e.target.value;
                            updateRule();
                        }}
                    />
                </Grid>
            );
            case "TIMESTAMP_LESS_THAN": return (
                <Grid xs={1} lg={1}>
                    <DatePicker
                        sx={{ width: "100% "}}
                        label="Value"
                        value={dayjs(rule.timestampLessThan) ?? ""}
                        onChange={(newValue) => {
                            rule.timestampLessThan = newValue;
                            updateRule();
                        }}
                    />
                </Grid>
            );
            case "NUMERIC_LESS_THAN": return (
                <Grid xs={1} lg={1}>
                    <TextField
                        sx={{ width: "100%" }}
                        label={"Value"}
                        type="number"
                        value={rule.numericLessThan ?? 0}
                        onChange={(e) => {
                            rule.numericLessThan = e.target.value;
                            updateRule();
                        }}
                    />
                </Grid>
            )
            case "TIMESTAMP_BETWEEN": return (
                <>
                    <Grid xs={1} lg={1}>
                        <DatePicker
                            sx={{ width: "100% "}}
                            label="Value"
                            value={dayjs(rule.timestampGreaterThan) ?? ""}
                            onChange={(newValue) => {
                                rule.timestampGreaterThan = newValue;
                                updateRule();
                            }}
                        />
                    </Grid>
                    <Grid xs={1} lg={1} display="flex" justifyContent="center" alignItems="center">
                        <Typography fontSize={"1.25em"}>and</Typography>
                    </Grid>
                    <Grid xs={1} lg={1}>
                        <DatePicker
                            sx={{ width: "100% "}}
                            label="Value"
                            value={dayjs(rule.timestampLessThan) ?? ""}
                            onChange={(newValue) => {
                                rule.timestampLessThan = newValue;
                                updateRule();
                            }}
                        />
                    </Grid>
                </>
            );
            case "NUMERIC_BETWEEN": return (
                <>
                    <Grid xs={1} lg={1}>
                        <TextField
                            sx={{ width: "100%" }}
                            label={"Greater Than"}
                            type="number"
                            value={rule.numericGreaterThan ?? 0}
                            onChange={(e) => {
                                rule.numericGreaterThan = e.target.value;
                                updateRule();
                            }}
                        />
                    </Grid>
                    <Grid xs={1} lg={1} display="flex" justifyContent="center" alignItems="center">
                        <Typography fontSize={"1.25em"}>and</Typography>
                    </Grid>
                    <Grid xs={1} lg={1}>
                        <TextField
                            sx={{ width: "100%" }}
                            label={"Less Than"}
                            type="number"
                            value={rule.numericLessThan ?? 0}
                            onChange={(e) => {
                                rule.numericLessThan = e.target.value;
                                updateRule();
                            }}
                        />
                    </Grid>
                </>
            );
            default: return ""; // Unimplemented rule type
        }
    }

    return renderCategorization();
}