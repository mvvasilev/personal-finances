import {useEffect, useState} from "react";
import utils from "@/utils.js";
import toast from "react-hot-toast";
import Grid from "@mui/material/Unstable_Grid2";
import Button from "@mui/material/Button";
import {
    Add as AddIcon,
    ChevronRight,
    ExpandMore,
    Save as SaveIcon
} from "@mui/icons-material";
import {TreeView} from "@mui/x-tree-view";
import CategorizationRule from "@/components/categories/CategorizationRule.jsx";
import {MenuItem, Select} from "@mui/material";
import Typography from "@mui/material/Typography";

export default function CategorizationRulesEditor({selectedCategory, onRuleBehaviorSelect, onSave}) {
    const [ruleTypes, setRuleTypes] = useState([]);
    const [fields, setFields] = useState([]);
    const [rules, setRules] = useState([]);

    useEffect(() => {
        utils.showSpinner();

        toast.promise(
            Promise.all([
                utils.performRequest("/api/categories/rules")
                    .then(resp => resp.json())
                    .then(({result}) => setRuleTypes(result)),
                utils.performRequest("/api/processed-transactions/fields")
                    .then(resp => resp.json())
                    .then(({result}) => setFields(result))
            ]),
            {
                loading: "Loading...",
                success: () => {
                    utils.hideSpinner();

                    return "Ready";
                },
                error: (err) => {
                    utils.hideSpinner();

                    return `Uh oh! Something went wrong: ${err}`;
                }
            }
        )
    }, []);

    useEffect(() => {
        utils.showSpinner();

        utils.performRequest(`/api/categories/${selectedCategory.id}/rules`)
            .then(resp => resp.json())
            .then(({result}) => {
                setRules(result);

                utils.hideSpinner();
            })
    }, [selectedCategory]);

    function createNewRule(e) {
        setRules(rules.concat({
            id: utils.generateUUID()
        }));
    }

    console.log(rules);

    function saveRules() {
        toast.promise(
            utils.performRequest(`/api/categories/${selectedCategory.id}/rules`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(rules.map(rule => mapRule(rule)))
            }).then(resp => onSave()),
            {
                loading: "Saving...",
                success: "Saved",
                error: (err) => `Uh oh, something went wrong: ${err}`
            }
        )
    }

    function mapRule(rule) {
        return {
            rule: rule.rule.rule,
            ruleBasedOn: rule.ruleBasedOn?.field,
            booleanValue: rule.booleanValue,
            stringValue: rule.stringValue,
            numericValue: rule.numericValue,
            numericGreaterThan: rule.numericGreaterThan,
            numericLessThan: rule.numericLessThan,
            timestampGreaterThan: rule.timestampGreaterThan,
            timestampLessThan: rule.timestampLessThan,
            left: rule.left ? mapRule(rule.left) : null,
            right: rule.right ? mapRule(rule.right) : null
        };
    }

    return (
        <Grid container spacing={1}>

            <Grid container xs={12} lg={12}>
                <Grid xs={1} lg={1}>
                    <Button
                        sx={{ width: "100%", height: "100%" }}
                        variant="contained"
                        startIcon={<AddIcon />}
                        onClick={createNewRule}
                    >
                        Add Rule
                    </Button>
                </Grid>
                <Grid xs={1} lg={1}>
                    <Select
                        size="small"
                        sx={{ width: "100%", height: "100%" }}
                        defaultValue={"ANY"}
                        value={selectedCategory.ruleBehavior ?? "ANY"}
                        label={"Rules Behavior"}
                        onChange={(e) => onRuleBehaviorSelect(e.target.value)}
                    >
                        <MenuItem value="ALL">All</MenuItem>
                        <MenuItem value="ANY">Any</MenuItem>
                        <MenuItem value="NONE">None</MenuItem>
                    </Select>
                </Grid>
                <Grid xs={1} lg={1}>
                    <Button
                        sx={{ width: "100%", height: "100%" }}
                        variant="contained"
                        startIcon={<SaveIcon />}
                        onClick={saveRules}
                    >
                        Save Rules
                    </Button>
                </Grid>
                <Grid xs={9} lg={9}></Grid>
            </Grid>

            {rules.map((r, i) => (
                <Grid key={`rule-${i}`} xs={12} lg={12}>
                    <TreeView
                        defaultCollapseIcon={<ExpandMore />}
                        defaultExpandIcon={<ChevronRight />}
                    >
                        <CategorizationRule
                            key={r.id}
                            ruleData={r}
                            fields={fields}
                            ruleTypes={ruleTypes}
                            onDelete={() => setRules(rules.filter(rr => rr.id !== r.id))}
                            updateRuleData={(ruleData) => {
                                setRules(rules.map(rr => rr.id === r.id ? ruleData : rr))
                            }}
                        />
                    </TreeView>
                </Grid>
            ))}
        </Grid>
    );
}