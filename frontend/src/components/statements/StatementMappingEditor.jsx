import Grid from "@mui/material/Unstable_Grid2";
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import {FormHelperText, Input, MenuItem, Select} from "@mui/material";
import utils from "@/utils.js";
import {useEffect, useState} from "react";
import Button from "@mui/material/Button";
import Box from "@mui/material/Box";
import toast from "react-hot-toast";

const FIELD_TYPES = [
    "STRING",
    "NUMERIC",
    "TIMESTAMP",
    "BOOLEAN"
];

const CONVERSION_TYPES = {
    STRING_TO_BOOLEAN: "STRING_TO_BOOLEAN"
}

const UNMAPPED_FIELD_NAME = "UNMAPPED";

const UNMAPPED_FIELDS = FIELD_TYPES.map(type => {
    return {
        field: UNMAPPED_FIELD_NAME,
        type
    }
});

const NONE_CONVERSION_TYPE = "NONE";

const NONE_CONVERSIONS = FIELD_TYPES.flatMap(fromType => {
    return FIELD_TYPES.map(toType => {
        return {
            type: NONE_CONVERSION_TYPE,
            from: fromType,
            to: toType
        }
    });
});

export default function StatementMappingEditor({statementId}) {
    const [mappings, setMappings] = useState([]);

    const [fields, setFields] = useState([]);

    const [supportedConversions, setSupportedConversions] = useState([]);

    const [valueGroups, setValueGroups] = useState([]);

    const [existingMappings, setExistingMappings] = useState([]);

    useEffect(() => {
        let supportedConversionsPromise = utils.performRequest("/api/statements/supported-conversions")
            .then(resp => resp.json())
            .then(({result}) => setSupportedConversions(result));

        let valueGroupsPromise = utils.performRequest(`/api/statements/${statementId}/transactionValueGroups`)
            .then(res => res.json())
            .then(json => setValueGroups(json.result));

        let existingMappingsPromise = utils.performRequest(`/api/statements/${statementId}/mappings`)
            .then(res => res.json())
            .then(json => setExistingMappings(json.result));

        let fieldsPromise = utils.performRequest("/api/processed-transactions/fields")
            .then(resp => resp.json())
            .then(({result}) => setFields(result));

        toast.promise(
            Promise.all([supportedConversionsPromise, valueGroupsPromise, existingMappingsPromise, fieldsPromise]),
            {
                loading: "Preparing...",
                success: "Ready",
                error: (err) => `Uh oh, something went wrong: ${err}`
            }
        );
    }, [statementId]);

    useEffect(() => {
        setMappings(valueGroups.map(g => {
            let existingMapping = existingMappings.find(m => m.rawTransactionValueGroupId === g.id);

            let unmappedField = UNMAPPED_FIELDS.find(f => f.type === g.type);
            let noneConversion = NONE_CONVERSIONS.find(c => c.from === g.type && c.to === g.type );

            return {
                valueGroup: {
                    id: g.id,
                    type: g.type,
                    name: g.name,
                    unmappedField,
                    noneConversion
                },
                conversion: {
                    options: supportedConversions
                        .concat(noneConversion)
                        .filter(c => c.from === g.type)
                        .map(c => {
                            return {
                                from: c.from,
                                to: c.to,
                                type: c.type
                            }
                        }),
                    selected: {
                        from: existingMapping?.conversionType?.from ?? noneConversion.from,
                        to: existingMapping?.conversionType?.to ?? noneConversion.to,
                        type: existingMapping?.conversionType?.type ?? noneConversion.type,
                        trueBranchStringValue: existingMapping?.trueBranchStringValue ?? ""
                    }
                },
                field: {
                    options: fields.concat(UNMAPPED_FIELDS)
                        .filter(f => f.type === (existingMapping?.conversionType?.to ?? noneConversion.to))
                        .map(f => {
                            return {
                                field: f.field,
                                type: f.type
                            }
                        }),
                    selected: {
                        field: existingMapping?.processedTransactionField?.field ?? unmappedField.field,
                        type: existingMapping?.processedTransactionField?.type ?? unmappedField.type,
                    }
                }
            };
        }));
    }, [supportedConversions, existingMappings, fields, valueGroups]);

    async function onSave(e) {
        let createMappingsDto = mappings
            .filter(m => m.field.selected.field !== UNMAPPED_FIELD_NAME)
            .map(m => {
                return {
                    rawTransactionValueGroupId: m.valueGroup.id,
                    field: m.field.selected.field,
                    conversionType: m.conversion.selected.type === NONE_CONVERSION_TYPE ? undefined : m.conversion.selected.type,
                    trueBranchStringValue: m.conversion.selected.trueBranchStringValue
                }
            });

        await toast.promise(
            utils.performRequest(`/api/statements/${statementId}/mappings`, {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(createMappingsDto)
            }),
            {
                loading: "Saving mappings...",
                success: "Saved",
                error: (err) => `Uh oh, something went wrong: ${err}`
            }
        );

        await toast.promise(
            utils.performRequest(`/api/statements/${statementId}/process`, {
                method: "POST"
            }),
            {
                loading: "Creating transactions...",
                success: "Done!",
                error: (err) => `Uh oh, something went wrong: ${err}`
            }
        );
    }

    function onConversionChange(e, mapping) {
        let newConversion = supportedConversions.find(c => c.type === e.target.value) ?? mapping.valueGroup.noneConversion;

        mapping.conversion.selected = newConversion;

        mapping.field.options = fields.concat(UNMAPPED_FIELDS)
            .filter(f => f.type === newConversion.to)
            .map(f => {
                return {
                    field: f.field,
                    type: f.type
                }
            });

        if (mapping.field.selected.type !== newConversion.to) {
            mapping.field.selected = mapping.valueGroup.unmappedField;
        }

        setMappings(mappings.map(m => {
            if (m.valueGroup.id === mapping.valueGroup.id) {
                return mapping;
            }

            return m;
        }));
    }

    function onFieldChange(e, mapping) {
        setMappings(mappings.map(m => {
            if (m.valueGroup.id === mapping.valueGroup.id) {
                mapping.field.selected = fields.find(c => c.field === e.target.value) ?? mapping.valueGroup.unmappedField;
            }

            return m;
        }));
    }

    function renderConversion(mapping, type) {
        switch (type) {
            case CONVERSION_TYPES.STRING_TO_BOOLEAN:
                return (
                    <Box>
                        <Input
                            onChange={(e) => onChangeStringToBooleanTrueValue(e, mapping)}
                            value={mapping.conversion.selected.trueBranchStringValue}
                        /> = true, else false
                    </Box>
                )
            default: return (<p>Unsupported</p>)
        }
    }

    function onChangeStringToBooleanTrueValue(e, mapping) {
        setMappings(mappings.map(m => {
            if (m.valueGroup.id === mapping.valueGroup.id) {
                m.conversion.selected.trueBranchStringValue = e.target.value;
            }

            return m;
        }));
    }

    return (
        <Grid container columnSpacing={3}>
            <Grid container xs={12} lg={12}>
                <Grid xs={1} lg={1}>
                    <Button sx={{width: "100%"}} variant="contained" onClick={onSave}>
                        Save & Apply
                    </Button>
                </Grid>
                {/* TODO */}
                {/*<Grid xs={1} lg={1}>*/}
                {/*    <Button sx={{width: "100%"}} variant="contained">*/}
                {/*        Export*/}
                {/*    </Button>*/}
                {/*</Grid>*/}
                {/*<Grid xs={1} lg={1}>*/}
                {/*    <Button sx={{width: "100%"}} variant="contained">*/}
                {/*        Import*/}
                {/*    </Button>*/}
                {/*</Grid>*/}
            </Grid>

            <Grid xs={12} lg={12}>
                <TableContainer>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell style={{width: "300px"}} align="left" >Statement Column</TableCell>
                                <TableCell align="left">Conversion</TableCell>
                                <TableCell style={{width: "300px"}} align="left">Transaction Field</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {
                                mappings.map(m => (
                                    <TableRow key={m.valueGroup.id}>
                                        <TableCell>{m.valueGroup.name}</TableCell>
                                        <TableCell>
                                            <Grid columnSpacing={1} container>
                                                <Grid xs={2} lg={2}>
                                                    <Select
                                                        sx={{
                                                            width: "100%",
                                                            color: () => {
                                                                if (m.conversion.selected.type === NONE_CONVERSION_TYPE) {
                                                                    return 'gray'
                                                                } else {
                                                                    return undefined;
                                                                }
                                                            }
                                                        }}
                                                        defaultValue={m.valueGroup.noneConversion.type}
                                                        value={m.conversion.selected.type}
                                                        onChange={(e) => onConversionChange(e, m)}
                                                    >
                                                        {
                                                            m.conversion.options.map(c => (
                                                                <MenuItem
                                                                    key={`${m.valueGroup.name}-${c.type}`}
                                                                    value={c.type}
                                                                >
                                                                    { utils.toPascalCase(c.type.replace(/_/g, " ")) }
                                                                </MenuItem>
                                                            ))
                                                        }
                                                    </Select>
                                                </Grid>
                                                <Grid xs={10} lg={10}>
                                                    {
                                                        m.conversion.selected.type !== NONE_CONVERSION_TYPE &&
                                                        renderConversion(m, m.conversion.selected.type)
                                                    }
                                                </Grid>
                                            </Grid>
                                        </TableCell>
                                        <TableCell>
                                            <Select
                                                sx={{
                                                    width: "100%",
                                                    color: () => {
                                                        if (m.field.selected.field === UNMAPPED_FIELD_NAME) {
                                                            return 'gray'
                                                        } else {
                                                            return undefined;
                                                        }
                                                    }
                                                }}
                                                defaultValue={m.valueGroup.unmappedField.field}
                                                value={m.field.selected.field}
                                                onChange={(e) => onFieldChange(e, m)}
                                            >
                                                {
                                                    m.field.options.map(f => (
                                                        <MenuItem
                                                            key={`${m.valueGroup.name}-${f.field}`}
                                                            value={f.field}
                                                        >
                                                            { utils.toPascalCase(f.field.replace(/_/g, " ")) }
                                                        </MenuItem>
                                                    ))
                                                }
                                            </Select>
                                        </TableCell>
                                    </TableRow>
                                ))
                            }
                        </TableBody>
                    </Table>
                </TableContainer>
            </Grid>
        </Grid>
    );
}