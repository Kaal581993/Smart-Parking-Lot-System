import { isAxiosError } from 'axios'
import {
  Car,
  Check,
  ChevronDown,
  ChevronLeft,
  ChevronRight,
  ChevronUp,
  Database,
  Edit3,
  FileText,
  KeyRound,
  LoaderCircle,
  MapPin,
  ParkingSquare,
  Plus,
  RefreshCw,
  Search,
  Shield,
  SlidersHorizontal,
  Ticket,
  Trash2,
  UserRound,
  Users,
  X,
} from 'lucide-react'
import { useCallback, useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import client from '../api/client'

export type ResourceKey =
  | 'owners'
  | 'ownerAadhaar'
  | 'users'
  | 'roles'
  | 'privileges'
  | 'addresses'
  | 'licenses'
  | 'vehicleTypes'
  | 'vehicles'
  | 'parkingSlots'
  | 'parkingTickets'

type AnyRecord = Record<string, unknown>
type FormValue = string | number | boolean | string[]
type FormValues = Record<string, FormValue>

type Option = {
  value: string
  label: string
  hint?: string
}

type FieldType =
  | 'text'
  | 'email'
  | 'number'
  | 'password'
  | 'date'
  | 'select'
  | 'address'
  | 'radio'
  | 'checkbox'
  | 'multiselect'
  | 'textarea'

type FormContext = {
  records: Record<ResourceKey, AnyRecord[]>
}

type FieldDefinition = {
  name: string
  label: string
  type?: FieldType
  required?: boolean
  placeholder?: string
  helpText?: string
  defaultValue?: FormValue
  options?: (context: FormContext) => Option[]
  visible?: (mode: 'create' | 'edit') => boolean
  readOnlyOnCreate?: boolean
  readOnlyOnEdit?: boolean
  min?: number
  step?: number
  fullWidth?: boolean
}

type ColumnDefinition = {
  label: string
  path: string
  tone?: 'accent' | 'muted' | 'plain'
}

type ResourceDefinition = {
  key: ResourceKey
  label: string
  pluralLabel: string
  eyebrow: string
  description: string
  icon: typeof Database
  listPath: string
  paged?: boolean
  createPath: string
  updatePath: (id: string) => string
  deletePath: (id: string) => string
  id: (record: AnyRecord) => string
  columns: ColumnDefinition[]
  fields: FieldDefinition[]
  getFormValues: (record?: AnyRecord) => FormValues
  toPayload: (values: FormValues, context: FormContext, mode: 'create' | 'edit') => AnyRecord
}

type Notice = {
  tone: 'success' | 'error'
  message: string
}

type ModalState = {
  mode: 'create' | 'edit'
  record?: AnyRecord
}

const RESOURCE_KEYS: ResourceKey[] = [
  'owners',
  'ownerAadhaar',
  'users',
  'roles',
  'privileges',
  'addresses',
  'licenses',
  'vehicleTypes',
  'vehicles',
  'parkingSlots',
  'parkingTickets',
]

const PAGE_SIZE = 8

const STATE_OPTIONS: Option[] = [
  { value: 'ANDHRA_PRADESH', label: 'Andhra Pradesh', hint: 'AP' },
  { value: 'ARUNACHAL_PRADESH', label: 'Arunachal Pradesh', hint: 'AR' },
  { value: 'ASSAM', label: 'Assam', hint: 'AS' },
  { value: 'BIHAR', label: 'Bihar', hint: 'BR' },
  { value: 'CHHATTISGARH', label: 'Chhattisgarh', hint: 'CG' },
  { value: 'GOA', label: 'Goa', hint: 'GA' },
  { value: 'GUJARAT', label: 'Gujarat', hint: 'GJ' },
  { value: 'HARYANA', label: 'Haryana', hint: 'HR' },
  { value: 'HIMACHAL_PRADESH', label: 'Himachal Pradesh', hint: 'HP' },
  { value: 'JHARKHAND', label: 'Jharkhand', hint: 'JH' },
  { value: 'KARNATAKA', label: 'Karnataka', hint: 'KA' },
  { value: 'KERALA', label: 'Kerala', hint: 'KL' },
  { value: 'MADHYA_PRADESH', label: 'Madhya Pradesh', hint: 'MP' },
  { value: 'MAHARASHTRA', label: 'Maharashtra', hint: 'MH' },
  { value: 'MANIPUR', label: 'Manipur', hint: 'MN' },
  { value: 'MEGHALAYA', label: 'Meghalaya', hint: 'ML' },
  { value: 'MIZORAM', label: 'Mizoram', hint: 'MZ' },
  { value: 'NAGALAND', label: 'Nagaland', hint: 'NL' },
  { value: 'ODISHA', label: 'Odisha', hint: 'OD' },
  { value: 'PUNJAB', label: 'Punjab', hint: 'PB' },
  { value: 'RAJASTHAN', label: 'Rajasthan', hint: 'RJ' },
  { value: 'SIKKIM', label: 'Sikkim', hint: 'SK' },
  { value: 'TAMIL_NADU', label: 'Tamil Nadu', hint: 'TN' },
  { value: 'TELANGANA', label: 'Telangana', hint: 'TS' },
  { value: 'TRIPURA', label: 'Tripura', hint: 'TR' },
  { value: 'UTTAR_PRADESH', label: 'Uttar Pradesh', hint: 'UP' },
  { value: 'UTTARAKHAND', label: 'Uttarakhand', hint: 'UK' },
  { value: 'WEST_BENGAL', label: 'West Bengal', hint: 'WB' },
  { value: 'ANDAMAN_AND_NICOBAR', label: 'Andaman and Nicobar', hint: 'AN' },
  { value: 'CHANDIGARH', label: 'Chandigarh', hint: 'CH' },
  {
    value: 'DADRA_AND_NAGAR_HAVELI_AND_DAMAN_AND_DIU',
    label: 'Dadra and Nagar Haveli and Daman and Diu',
    hint: 'DD',
  },
  { value: 'DELHI', label: 'Delhi', hint: 'DL' },
  { value: 'JAMMU_AND_KASHMIR', label: 'Jammu and Kashmir', hint: 'JK' },
  { value: 'LADAKH', label: 'Ladakh', hint: 'LA' },
  { value: 'LAKSHADWEEP', label: 'Lakshadweep', hint: 'LD' },
  { value: 'PUDUCHERRY', label: 'Puducherry', hint: 'PY' },
]

const GENDER_OPTIONS: Option[] = [
  { value: 'MALE', label: 'Male' },
  { value: 'FEMALE', label: 'Female' },
  { value: 'TRANSMALE', label: 'Trans male' },
  { value: 'TRANSFEMALE', label: 'Trans female' },
  { value: 'NON_BINARY', label: 'Non-binary' },
]

const PAYMENT_OPTIONS: Option[] = [
  { value: 'PENDING', label: 'Pending' },
  { value: 'PAID', label: 'Paid' },
  { value: 'PARTIALLY_PAID', label: 'Partially paid' },
  { value: 'WAIVED_OFF', label: 'Waived off' },
]

const TICKET_STATUS_OPTIONS: Option[] = [
  { value: 'ACTIVE', label: 'Active' },
  { value: 'COMPLETED', label: 'Completed' },
  { value: 'CANCELLED', label: 'Cancelled' },
]

const VEHICLE_TYPE_PRESETS = [
  'Two Wheeler',
  'Three Wheeler',
  'Car Hatchback',
  'Car Sedan',
  'SUV',
  'Mini Bus',
  'Bus',
  'Truck',
  'Electric Two Wheeler',
  'Electric Car',
]

const emptyRecords = (): Record<ResourceKey, AnyRecord[]> => ({
  owners: [],
  ownerAadhaar: [],
  users: [],
  roles: [],
  privileges: [],
  addresses: [],
  licenses: [],
  vehicleTypes: [],
  vehicles: [],
  parkingSlots: [],
  parkingTickets: [],
})

function asRecord(value: unknown): AnyRecord {
  return value && typeof value === 'object' && !Array.isArray(value) ? (value as AnyRecord) : {}
}

function valueAt(record: AnyRecord, path: string): unknown {
  return path.split('.').reduce<unknown>((value, key) => {
    const current = asRecord(value)
    if (current[key] !== undefined) return current[key]
    const matchingKey = Object.keys(current).find((candidate) => candidate.toLowerCase() === key.toLowerCase())
    return matchingKey ? current[matchingKey] : undefined
  }, record)
}

function stringValue(value: unknown): string {
  if (value === null || value === undefined) return ''
  if (typeof value === 'boolean') return value ? 'Yes' : 'No'
  if (typeof value === 'object') return objectLabel(asRecord(value))
  return String(value)
}

function objectLabel(record: AnyRecord): string {
  const preferred = [
    'typeName',
    'numberPlate',
    'slotNumber',
    'username',
    'name',
    'vehicleOwnerName',
    'firstName',
    'aadhaarNo',
    'licenseId',
    'vehicleId',
    'slotId',
    'ticketId',
    'id',
  ]
  for (const key of preferred) {
    if (record[key] !== undefined && record[key] !== null && String(record[key]) !== '') {
      return String(record[key])
    }
  }
  const first = Object.values(record).find((item) => typeof item !== 'object' && item !== undefined)
  return first === undefined ? '—' : String(first)
}

function formatDateForInput(value: unknown): string {
  const source = stringValue(value)
  if (!source) return ''
  const slashParts = source.split('/')
  if (slashParts.length === 3 && slashParts[0].length <= 2) {
    return `${slashParts[2].slice(0, 4)}-${slashParts[1].padStart(2, '0')}-${slashParts[0].padStart(2, '0')}`
  }
  return source.slice(0, 10)
}

function formatDateForApi(value: unknown): string | undefined {
  const source = stringValue(value)
  if (!source) return undefined
  const parts = source.split('-')
  if (parts.length === 3 && parts[0].length === 4) return `${parts[2]}/${parts[1]}/${parts[0]}`
  return source
}

function numericValue(value: unknown, fallback = 0): number {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : fallback
}

function relationId(value: unknown, ...keys: string[]): string {
  const record = asRecord(value)
  for (const key of keys) {
    const nestedValue = key.includes('.') ? valueAt(record, key) : record[key]
    if (nestedValue !== undefined && nestedValue !== null && String(nestedValue) !== '') return String(nestedValue)
  }
  return ''
}

function apiError(error: unknown): string {
  if (isAxiosError(error)) {
    if (error.response?.status === 403) return 'You do not have the privilege required for this action.'
    if (error.response?.status === 401) return 'Your session has expired. Sign in again to continue.'
    const responseData = error.response?.data
    if (typeof responseData === 'string' && responseData.trim()) return responseData
    if (responseData && typeof responseData === 'object') {
      const data = responseData as AnyRecord
      const message = data.message || data.error || data.detail
      if (message) return String(message)
    }
    if (error.message) return error.message
  }
  return error instanceof Error ? error.message : 'The request could not be completed.'
}

function optionsFor(
  records: AnyRecord[],
  idPath: string,
  labelPath: string,
  hintPath?: string,
): Option[] {
  return records
    .map((record) => {
      const value = stringValue(valueAt(record, idPath))
      const label = stringValue(valueAt(record, labelPath)) || value
      const hint = hintPath ? stringValue(valueAt(record, hintPath)) : undefined
      return { value, label, hint }
    })
    .filter((option) => option.value)
}

function ownerAadhaar(record: AnyRecord): string {
  return relationId(record.aadhaar, 'aadhaarNo') || relationId(record.ownerIdentityId, 'aadharNo')
}

function ownerLicense(record: AnyRecord): string {
  return relationId(record.licenseNo, 'licenseId') || relationId(record.ownerIdentityId, 'licenseNo')
}

function addressId(record: AnyRecord): string {
  return stringValue(record.addressId) || relationId(record.address, 'addressId') || relationId(record.Address, 'addressId')
}

function defaultForm(fields: FieldDefinition[]): FormValues {
  return fields.reduce<FormValues>((values, field) => {
    if (field.type === 'checkbox') values[field.name] = field.defaultValue ?? false
    else if (field.type === 'multiselect') values[field.name] = field.defaultValue ?? []
    else values[field.name] = field.defaultValue ?? ''
    return values
  }, {})
}

const roleOptions = (context: FormContext) => optionsFor(context.records.roles, 'name', 'name', 'id')
const privilegeOptions = (context: FormContext) => optionsFor(context.records.privileges, 'id', 'name', 'id')
const addressOptions = (context: FormContext) =>
  context.records.addresses.map((record) => ({
    value: stringValue(record.addressId),
    label: `${stringValue(record.townOrCityName) || 'Address'} · ${stringValue(record.pincode)}`,
    hint: stringValue(record.addressId),
  }))
const aadhaarOptions = (context: FormContext) =>
  context.records.ownerAadhaar.map((record) => ({
    value: stringValue(record.aadhaarNo),
    label: `${stringValue(record.firstName)} ${stringValue(record.lastName)}`.trim() || stringValue(record.aadhaarNo),
    hint: stringValue(record.aadhaarNo),
  }))
const licenseOptions = (context: FormContext) =>
  context.records.licenses.map((record) => ({
    value: stringValue(record.licenseId),
    label: stringValue(record.licenseId) || 'License',
    hint: `${stringValue(record.stateCode)} ${stringValue(record.rtoDistrictCode)}`.trim(),
  }))
const vehicleTypeOptions = (context: FormContext) =>
  optionsFor(context.records.vehicleTypes, 'typeId', 'typeName', 'hourlyRate')
const vehicleOptions = (context: FormContext) =>
  context.records.vehicles.map((record) => ({
    value: stringValue(record.vehicleId),
    label: stringValue(record.numberPlate) || stringValue(record.vehicleId),
    hint: stringValue(valueAt(record, 'vehicleType.typeName')),
  }))
const slotOptions = (context: FormContext) =>
  context.records.parkingSlots.map((record) => ({
    value: stringValue(record.slotId),
    label: stringValue(record.slotNumber) || stringValue(record.slotId),
    hint: `Floor ${stringValue(record.floorNo)}`,
  }))

const RESOURCE_DEFINITIONS: Record<ResourceKey, ResourceDefinition> = {
  owners: {
    key: 'owners',
    label: 'Vehicle owner',
    pluralLabel: 'Owners',
    eyebrow: 'People',
    description: 'Manage owner identity, contact, license links, balances, and registered vehicles.',
    icon: Users,
    listPath: '/vehicle_owners',
    paged: true,
    createPath: '/vehicle_owners',
    updatePath: (id) => `/vehicle_owners/${encodeURIComponent(id)}`,
    deletePath: (id) => `/vehicle_owners/${encodeURIComponent(id)}`,
    id: ownerAadhaar,
    columns: [
      { label: 'Owner', path: 'vehicleOwnerName', tone: 'accent' },
      { label: 'Aadhaar', path: 'aadhaar.aadhaarNo' },
      { label: 'License', path: 'licenseNo.licenseId' },
      { label: 'Email', path: 'vehicleOwnerEmail' },
      { label: 'Dues', path: 'vehicleOwnerDues', tone: 'muted' },
    ],
    fields: [
      { name: 'vehicleOwnerName', label: 'Owner name', required: true },
      { name: 'vehicleOwnerPhone', label: 'Phone', type: 'text', required: true, placeholder: '10 digit number' },
      { name: 'vehicleOwnerEmail', label: 'Email', type: 'email', required: true },
      { name: 'aadhaarNo', label: 'Aadhaar', type: 'select', required: true, readOnlyOnEdit: true, options: aadhaarOptions },
      { name: 'licenseId', label: 'Driving license', type: 'select', required: true, readOnlyOnEdit: true, options: licenseOptions },
      { name: 'vehicleOwnerPanNo', label: 'PAN', required: true, placeholder: 'ABCDE1234F' },
      { name: 'vehicleOwnerDOB', label: 'Date of birth', type: 'date', required: true },
      { name: 'addressId', label: 'Address', type: 'select', required: true, options: addressOptions },
      { name: 'licenseExpiryDate', label: 'License expiry', type: 'date', required: true },
      { name: 'vehicleOwnerLedgerBalance', label: 'Ledger balance', type: 'number', required: true, step: 0.01 },
      { name: 'vehicleOwnerDues', label: 'Dues', type: 'number', required: true, step: 0.01 },
    ],
    getFormValues: (record) => ({
      vehicleOwnerName: stringValue(record?.vehicleOwnerName),
      vehicleOwnerPhone: stringValue(record?.vehicleOwnerPhone),
      vehicleOwnerEmail: stringValue(record?.vehicleOwnerEmail),
      aadhaarNo: ownerAadhaar(record || {}),
      licenseId: ownerLicense(record || {}),
      vehicleOwnerPanNo: stringValue(record?.vehicleOwnerPanNo),
      vehicleOwnerDOB: formatDateForInput(record?.vehicleOwnerDOB),
      addressId: relationId(record?.vehicleOwnerAddress, 'addressId') || stringValue(record?.addressId),
      licenseExpiryDate: formatDateForInput(record?.licenseExpiryDate),
      vehicleOwnerLedgerBalance: numericValue(record?.vehicleOwnerLedgerBalance),
      vehicleOwnerDues: numericValue(record?.vehicleOwnerDues),
    }),
    toPayload: (values) => ({
      vehicleOwnerName: stringValue(values.vehicleOwnerName),
      vehicleOwnerPhone: numericValue(values.vehicleOwnerPhone),
      vehicleOwnerEmail: stringValue(values.vehicleOwnerEmail),
      aadhaar: { aadhaarNo: stringValue(values.aadhaarNo) },
      licenseNo: { licenseId: stringValue(values.licenseId) },
      ownerIdentityId: {
        aadharNo: stringValue(values.aadhaarNo),
        licenseNo: stringValue(values.licenseId),
      },
      vehicleOwnerPanNo: stringValue(values.vehicleOwnerPanNo).toUpperCase(),
      vehicleOwnerDOB: formatDateForApi(values.vehicleOwnerDOB),
      vehicleOwnerAddress: { addressId: stringValue(values.addressId) },
      licenseExpiryDate: formatDateForApi(values.licenseExpiryDate),
      vehicleOwnerLedgerBalance: numericValue(values.vehicleOwnerLedgerBalance),
      vehicleOwnerDues: numericValue(values.vehicleOwnerDues),
    }),
  },
  ownerAadhaar: {
    key: 'ownerAadhaar',
    label: 'Owner Aadhaar',
    pluralLabel: 'Owner Aadhaar',
    eyebrow: 'People',
    description: 'Store the legal owner identity and contact record linked to an address.',
    icon: UserRound,
    listPath: '/aadhaar/get_aadhaar',
    createPath: '/aadhaar/add_aadhaar',
    updatePath: () => '/aadhaar/update_aadhaar',
    deletePath: (id) => `/aadhaar/delete_aadhaar/${encodeURIComponent(id)}`,
    id: (record) => stringValue(record.aadhaarNo),
    columns: [
      { label: 'Aadhaar', path: 'aadhaarNo', tone: 'accent' },
      { label: 'Name', path: 'firstName' },
      { label: 'Gender', path: 'gender' },
      { label: 'Email', path: 'email' },
      { label: 'Address', path: 'address.townOrCityName' },
    ],
    fields: [
      { name: 'aadhaarNo', label: 'Aadhaar number', required: true, readOnlyOnEdit: true },
      { name: 'firstName', label: 'First name', required: true },
      { name: 'middleName', label: 'Middle name', required: true },
      { name: 'lastName', label: 'Last name', required: true },
      { name: 'gender', label: 'Gender', type: 'radio', required: true, options: () => GENDER_OPTIONS },
      { name: 'dateOfBirth', label: 'Date of birth', type: 'date', required: true },
      { name: 'email', label: 'Email', type: 'email', required: true },
      { name: 'phone', label: 'Phone', type: 'text' },
      { name: 'addressId', label: 'Address', type: 'address', required: true, helpText: 'Add the address here and it will be linked to this Aadhaar record.' },
      { name: 'panNo', label: 'PAN', required: true },
    ],
    getFormValues: (record) => ({
      aadhaarNo: stringValue(record?.aadhaarNo),
      firstName: stringValue(record?.firstName),
      middleName: stringValue(record?.middleName),
      lastName: stringValue(record?.lastName),
      gender: stringValue(record?.gender),
      dateOfBirth: formatDateForInput(record?.dateOfBirth),
      email: stringValue(record?.email),
      phone: stringValue(record?.phone),
      addressId: addressId(record || {}),
      panNo: stringValue(record?.panNo),
    }),
    toPayload: (values) => ({
      aadhaarNo: stringValue(values.aadhaarNo),
      firstName: stringValue(values.firstName),
      middleName: stringValue(values.middleName),
      lastName: stringValue(values.lastName),
      gender: stringValue(values.gender),
      dateOfBirth: formatDateForApi(values.dateOfBirth),
      email: stringValue(values.email),
      phone: stringValue(values.phone),
      addressId: stringValue(values.addressId),
      address: { addressId: stringValue(values.addressId) },
      panNo: stringValue(values.panNo).toUpperCase(),
    }),
  },
  users: {
    key: 'users',
    label: 'Application user',
    pluralLabel: 'Users',
    eyebrow: 'Access',
    description: 'Manage login accounts, role assignment, and the owner scope attached to each account.',
    icon: UserRound,
    listPath: '/auth/admin/users',
    createPath: '/auth/admin/users',
    updatePath: (id) => `/auth/admin/users/${encodeURIComponent(id)}`,
    deletePath: (id) => `/auth/admin/users/${encodeURIComponent(id)}`,
    id: (record) => stringValue(record.id),
    columns: [
      { label: 'Username', path: 'username', tone: 'accent' },
      { label: 'Role', path: 'role.name' },
      { label: 'Owner scope', path: 'ownerId' },
      { label: 'User ID', path: 'id', tone: 'muted' },
    ],
    fields: [
      { name: 'id', label: 'User ID', type: 'text', readOnlyOnEdit: true, visible: (mode) => mode === 'edit' },
      { name: 'username', label: 'Username', required: true },
      { name: 'password', label: 'Password', type: 'password', required: true, helpText: 'Leave blank while editing to keep the current password.' },
      { name: 'roleName', label: 'Role', type: 'select', required: true, options: roleOptions },
      { name: 'ownerId', label: 'Owner Aadhaar', type: 'select', options: aadhaarOptions, helpText: 'Optional for administrators; required for owner scoped accounts.' },
    ],
    getFormValues: (record) => ({
      id: stringValue(record?.id),
      username: stringValue(record?.username),
      password: '',
      roleName: stringValue(valueAt(record || {}, 'role.name')),
      ownerId: stringValue(record?.ownerId),
    }),
    toPayload: (values, _context, mode) => {
      const payload: AnyRecord = {
        username: stringValue(values.username),
        role: stringValue(values.roleName),
        ownerId: stringValue(values.ownerId) || undefined,
      }
      if (mode === 'create' || stringValue(values.password)) payload.password = stringValue(values.password)
      return payload
    },
  },
  roles: {
    key: 'roles',
    label: 'Role',
    pluralLabel: 'Roles',
    eyebrow: 'Access',
    description: 'Define named access roles and assign the privileges that travel with them.',
    icon: Shield,
    listPath: '/auth/admin/roles',
    createPath: '/auth/admin/roles',
    updatePath: (id) => `/auth/admin/roles/${encodeURIComponent(id)}`,
    deletePath: (id) => `/auth/admin/roles/${encodeURIComponent(id)}`,
    id: (record) => stringValue(record.id),
    columns: [
      { label: 'Role', path: 'name', tone: 'accent' },
      { label: 'Privileges', path: 'privileges', tone: 'plain' },
      { label: 'Role ID', path: 'id', tone: 'muted' },
    ],
    fields: [
      { name: 'id', label: 'Role ID', type: 'text', readOnlyOnEdit: true, visible: (mode) => mode === 'edit' },
      { name: 'name', label: 'Role name', required: true, placeholder: 'OPERATIONS' },
      { name: 'privilegeIds', label: 'Privileges', type: 'multiselect', options: privilegeOptions, helpText: 'Choose the permissions this role should carry.' },
    ],
    getFormValues: (record) => ({
      id: stringValue(record?.id),
      name: stringValue(record?.name),
      privilegeIds: Array.isArray(record?.privileges)
        ? record.privileges.map((privilege) => stringValue(asRecord(privilege).id)).filter(Boolean)
        : [],
    }),
    toPayload: (values) => ({
      id: values.id ? numericValue(values.id) : undefined,
      name: stringValue(values.name).toUpperCase(),
      privilegeIds: Array.isArray(values.privilegeIds) ? values.privilegeIds.map((id) => numericValue(id)) : [],
    }),
  },
  privileges: {
    key: 'privileges',
    label: 'Privilege',
    pluralLabel: 'Privileges',
    eyebrow: 'Access',
    description: 'Maintain the atomic permissions available to roles in the parking system.',
    icon: KeyRound,
    listPath: '/auth/admin/privileges',
    createPath: '/auth/admin/privileges',
    updatePath: (id) => `/auth/admin/privileges/${encodeURIComponent(id)}`,
    deletePath: (id) => `/auth/admin/privileges/${encodeURIComponent(id)}`,
    id: (record) => stringValue(record.id),
    columns: [
      { label: 'Privilege', path: 'name', tone: 'accent' },
      { label: 'Privilege ID', path: 'id', tone: 'muted' },
    ],
    fields: [
      { name: 'id', label: 'Privilege ID', type: 'text', readOnlyOnEdit: true, visible: (mode) => mode === 'edit' },
      { name: 'name', label: 'Privilege name', required: true, placeholder: 'MANAGE_VEHICLES' },
    ],
    getFormValues: (record) => ({ id: stringValue(record?.id), name: stringValue(record?.name) }),
    toPayload: (values) => ({
      id: values.id ? numericValue(values.id) : undefined,
      name: stringValue(values.name).toUpperCase(),
    }),
  },
  addresses: {
    key: 'addresses',
    label: 'Address',
    pluralLabel: 'Addresses',
    eyebrow: 'Owner records',
    description: 'Keep owner addresses normalized so Aadhaar and owner records can reuse them.',
    icon: MapPin,
    listPath: '/address/get_address',
    createPath: '/address/add_address',
    updatePath: () => '/address/update_address',
    deletePath: (id) => `/address/delete_address/${encodeURIComponent(id)}`,
    id: (record) => stringValue(record.addressId),
    columns: [
      { label: 'Address ID', path: 'addressId', tone: 'accent' },
      { label: 'City', path: 'townOrCityName' },
      { label: 'State', path: 'stateCode' },
      { label: 'Country', path: 'countryName' },
      { label: 'Pincode', path: 'pincode', tone: 'muted' },
    ],
    fields: [
      { name: 'addressId', label: 'Address ID', type: 'text', readOnlyOnEdit: true, visible: (mode) => mode === 'edit' },
      { name: 'flatNoHouseNo', label: 'Flat / house number', required: true },
      { name: 'societyNameAptName', label: 'Society / apartment', required: true },
      { name: 'areaName', label: 'Area', required: true },
      { name: 'townOrCityName', label: 'Town / city', required: true },
      { name: 'stateCode', label: 'State', type: 'select', required: true, options: () => STATE_OPTIONS },
      { name: 'countryName', label: 'Country', required: true, defaultValue: 'India' },
      { name: 'pincode', label: 'Pincode', required: true },
    ],
    getFormValues: (record) => ({
      addressId: stringValue(record?.addressId),
      flatNoHouseNo: stringValue(record?.flatNoHouseNo),
      societyNameAptName: stringValue(record?.societyNameAptName),
      areaName: stringValue(record?.areaName),
      townOrCityName: stringValue(record?.townOrCityName),
      stateCode: stringValue(record?.stateCode),
      countryName: stringValue(record?.countryName) || 'India',
      pincode: stringValue(record?.pincode),
    }),
    toPayload: (values, _context, mode) => ({
      ...(mode === 'edit' ? { addressId: stringValue(values.addressId) } : {}),
      flatNoHouseNo: stringValue(values.flatNoHouseNo),
      societyNameAptName: stringValue(values.societyNameAptName),
      areaName: stringValue(values.areaName),
      townOrCityName: stringValue(values.townOrCityName),
      stateCode: stringValue(values.stateCode),
      countryName: stringValue(values.countryName),
      pincode: stringValue(values.pincode),
    }),
  },
  licenses: {
    key: 'licenses',
    label: 'Driving license',
    pluralLabel: 'Licenses',
    eyebrow: 'Owner records',
    description: 'Create and update license components with the backend state enum and a generated license ID.',
    icon: FileText,
    listPath: '/driving_license/get_license',
    createPath: '/driving_license/add_license',
    updatePath: () => '/driving_license/update_license',
    deletePath: (id) => `/driving_license/delete_license/${encodeURIComponent(id)}`,
    id: (record) => stringValue(record.licenseId),
    columns: [
      { label: 'License no.', path: 'licenseId', tone: 'accent' },
      { label: 'State', path: 'stateCode' },
      { label: 'RTO', path: 'rtoDistrictCode' },
      { label: 'Series', path: 'seriesCode' },
      { label: 'Serial', path: 'serialNumber', tone: 'muted' },
    ],
    fields: [
      { name: 'licenseId', label: 'License ID', type: 'text', readOnlyOnEdit: true, visible: (mode) => mode === 'edit', helpText: 'Generated by the backend from the license components.' },
      { name: 'stateCode', label: 'State', type: 'select', required: true, options: () => STATE_OPTIONS },
      { name: 'rtoDistrictCode', label: 'RTO district code', required: true, placeholder: '01' },
      { name: 'seriesCode', label: 'Series', required: true, placeholder: 'AB' },
      { name: 'serialNumber', label: 'Serial number', required: true, placeholder: '1234' },
    ],
    getFormValues: (record) => ({
      licenseId: stringValue(record?.licenseId),
      stateCode: stringValue(record?.stateCode),
      rtoDistrictCode: stringValue(record?.rtoDistrictCode),
      seriesCode: stringValue(record?.seriesCode),
      serialNumber: stringValue(record?.serialNumber),
    }),
    toPayload: (values, _context, mode) => ({
      ...(mode === 'edit' ? { licenseId: stringValue(values.licenseId) } : {}),
      stateCode: stringValue(values.stateCode),
      rtoDistrictCode: stringValue(values.rtoDistrictCode),
      seriesCode: stringValue(values.seriesCode).toUpperCase(),
      serialNumber: stringValue(values.serialNumber),
    }),
  },
  vehicleTypes: {
    key: 'vehicleTypes',
    label: 'Vehicle type',
    pluralLabel: 'Vehicle types',
    eyebrow: 'Fleet',
    description: 'Set the hourly and daily rate used by slot allocation and fare calculation.',
    icon: SlidersHorizontal,
    listPath: '/vehicle_type/get_vehicle_type',
    createPath: '/vehicle_type/add_vehicle_type',
    updatePath: (id) => `/vehicle_type/update_vehicle_type/${encodeURIComponent(id)}`,
    deletePath: (id) => `/vehicle_type/delete_vehicle_type/${encodeURIComponent(id)}`,
    id: (record) => stringValue(record.typeId),
    columns: [
      { label: 'Type', path: 'typeName', tone: 'accent' },
      { label: 'Hourly rate', path: 'hourlyRate' },
      { label: 'Daily rate', path: 'dailyRate' },
      { label: 'Type ID', path: 'typeId', tone: 'muted' },
    ],
    fields: [
      { name: 'typeId', label: 'Type ID', type: 'text', readOnlyOnEdit: true, visible: (mode) => mode === 'edit', helpText: 'Generated when a new type is created.' },
      { name: 'typeName', label: 'Type name', required: true, placeholder: 'Car Sedan', helpText: 'Start typing to use a preset or enter a custom fleet type.' },
      { name: 'hourlyRate', label: 'Hourly rate', type: 'number', required: true, min: 0, step: 0.01 },
      { name: 'dailyRate', label: 'Daily rate', type: 'number', required: true, min: 0, step: 0.01 },
    ],
    getFormValues: (record) => ({
      typeId: stringValue(record?.typeId),
      typeName: stringValue(record?.typeName),
      hourlyRate: numericValue(record?.hourlyRate),
      dailyRate: numericValue(record?.dailyRate),
    }),
    toPayload: (values, _context, mode) => ({
      ...(mode === 'edit' ? { typeId: stringValue(values.typeId) } : {}),
      typeName: stringValue(values.typeName),
      hourlyRate: numericValue(values.hourlyRate),
      dailyRate: numericValue(values.dailyRate),
    }),
  },
  vehicles: {
    key: 'vehicles',
    label: 'Vehicle',
    pluralLabel: 'Vehicles',
    eyebrow: 'Fleet',
    description: 'Register vehicles with a number plate, type, owner, make, and model.',
    icon: Car,
    listPath: '/vehicles',
    paged: true,
    createPath: '/vehicles',
    updatePath: (id) => `/vehicles/${encodeURIComponent(id)}`,
    deletePath: (id) => `/vehicles/${encodeURIComponent(id)}`,
    id: (record) => stringValue(record.vehicleId),
    columns: [
      { label: 'Number plate', path: 'numberPlate', tone: 'accent' },
      { label: 'Type', path: 'vehicleType.typeName' },
      { label: 'Owner', path: 'owner.aadhaar.aadhaarNo' },
      { label: 'Brand / model', path: 'vehicleBrand' },
      { label: 'Vehicle ID', path: 'vehicleId', tone: 'muted' },
    ],
    fields: [
      { name: 'vehicleId', label: 'Vehicle ID', type: 'text', readOnlyOnEdit: true, visible: (mode) => mode === 'edit' },
      { name: 'numberPlate', label: 'Number plate', required: true, placeholder: 'MH01AB1234' },
      { name: 'vehicleTypeId', label: 'Vehicle type', type: 'select', required: true, options: vehicleTypeOptions },
      { name: 'ownerAadhaarNo', label: 'Owner', type: 'select', required: true, options: aadhaarOptions },
      { name: 'vehicleBrand', label: 'Brand' },
      { name: 'vehicleModel', label: 'Model' },
      { name: 'deleted', label: 'Marked deleted', type: 'checkbox' },
    ],
    getFormValues: (record) => ({
      vehicleId: stringValue(record?.vehicleId),
      numberPlate: stringValue(record?.numberPlate),
      vehicleTypeId: relationId(record?.vehicleType, 'typeId'),
      ownerAadhaarNo: relationId(valueAt(record || {}, 'owner.aadhaar'), 'aadhaarNo') || ownerAadhaar(asRecord(record?.owner)),
      vehicleBrand: stringValue(record?.vehicleBrand),
      vehicleModel: stringValue(record?.vehicleModel),
      deleted: Boolean(record?.deleted ?? record?.isDeleted),
    }),
    toPayload: (values, _context, mode) => ({
      ...(mode === 'edit' ? { vehicleId: stringValue(values.vehicleId) } : {}),
      numberPlate: stringValue(values.numberPlate).toUpperCase(),
      vehicleType: { typeId: stringValue(values.vehicleTypeId) },
      owner: { aadhaar: { aadhaarNo: stringValue(values.ownerAadhaarNo) } },
      vehicleBrand: stringValue(values.vehicleBrand),
      vehicleModel: stringValue(values.vehicleModel),
      isDeleted: Boolean(values.deleted),
    }),
  },
  parkingSlots: {
    key: 'parkingSlots',
    label: 'Parking slot',
    pluralLabel: 'Parking slots',
    eyebrow: 'Operations',
    description: 'Manage floor capacity, permitted vehicle type, rates, and slot availability.',
    icon: ParkingSquare,
    listPath: '/parking_slots',
    paged: true,
    createPath: '/parking_slots',
    updatePath: (id) => `/parking_slots/${encodeURIComponent(id)}`,
    deletePath: (id) => `/parking_slots/${encodeURIComponent(id)}`,
    id: (record) => stringValue(record.slotId),
    columns: [
      { label: 'Slot', path: 'slotNumber', tone: 'accent' },
      { label: 'Floor', path: 'floorNo' },
      { label: 'Allowed type', path: 'vehicleTypeAllowed.typeName' },
      { label: 'Rate / hour', path: 'costPerHour' },
      { label: 'Availability', path: 'available', tone: 'muted' },
    ],
    fields: [
      { name: 'slotId', label: 'Slot ID', type: 'text', readOnlyOnEdit: true, visible: (mode) => mode === 'edit' },
      { name: 'slotNumber', label: 'Slot number', required: true, placeholder: 'P-101' },
      { name: 'floorNo', label: 'Floor number', type: 'number', required: true, min: 1, step: 1 },
      { name: 'floorLimit', label: 'Floor limit', type: 'number', required: true, min: 1, step: 1 },
      { name: 'floorSlotCapacity', label: 'Floor slot capacity', type: 'number', required: true, min: 1, step: 1 },
      { name: 'costPerHour', label: 'Cost per hour', type: 'number', required: true, min: 0, step: 0.01 },
      { name: 'vehicleTypeId', label: 'Allowed vehicle type', type: 'select', options: vehicleTypeOptions },
      { name: 'allocationDuration', label: 'Allocation duration', placeholder: 'PT2H', helpText: 'ISO-8601 duration, for example PT2H.' },
      { name: 'available', label: 'Available', type: 'checkbox', defaultValue: true },
      { name: 'active', label: 'Active', type: 'checkbox', defaultValue: true },
    ],
    getFormValues: (record) => ({
      slotId: stringValue(record?.slotId),
      slotNumber: stringValue(record?.slotNumber),
      floorNo: numericValue(record?.floorNo),
      floorLimit: numericValue(record?.floorLimit),
      floorSlotCapacity: numericValue(record?.floorSlotCapacity),
      costPerHour: numericValue(record?.costPerHour),
      vehicleTypeId: relationId(record?.vehicleTypeAllowed, 'typeId'),
      allocationDuration: stringValue(record?.allocationDuration),
      available: Boolean(record?.available ?? record?.isAvailable ?? true),
      active: Boolean(record?.active ?? record?.isActive ?? true),
    }),
    toPayload: (values, _context, mode) => ({
      ...(mode === 'edit' ? { slotId: stringValue(values.slotId) } : {}),
      slotNumber: stringValue(values.slotNumber),
      floorNo: numericValue(values.floorNo),
      floorLimit: numericValue(values.floorLimit),
      floorSlotCapacity: numericValue(values.floorSlotCapacity),
      costPerHour: numericValue(values.costPerHour),
      vehicleTypeAllowed: values.vehicleTypeId ? { typeId: stringValue(values.vehicleTypeId) } : undefined,
      allocationDuration: stringValue(values.allocationDuration) || undefined,
      available: Boolean(values.available),
      active: Boolean(values.active),
    }),
  },
  parkingTickets: {
    key: 'parkingTickets',
    label: 'Parking ticket',
    pluralLabel: 'Parking tickets',
    eyebrow: 'Operations',
    description: 'Review the complete ticket lifecycle, fee state, payment state, and linked records.',
    icon: Ticket,
    listPath: '/parking_tickets',
    paged: true,
    createPath: '/parking_tickets',
    updatePath: (id) => `/parking_tickets/${encodeURIComponent(id)}`,
    deletePath: (id) => `/parking_tickets/${encodeURIComponent(id)}`,
    id: (record) => stringValue(record.ticketId),
    columns: [
      { label: 'Ticket', path: 'ticketId', tone: 'accent' },
      { label: 'Vehicle', path: 'vehicleDetails.numberPlate' },
      { label: 'Slot', path: 'parkingSlot.slotNumber' },
      { label: 'Status', path: 'status' },
      { label: 'Total fee', path: 'totalFee', tone: 'muted' },
    ],
    fields: [
      { name: 'ticketId', label: 'Ticket ID', type: 'text', readOnlyOnEdit: true, visible: (mode) => mode === 'edit' },
      { name: 'vehicleId', label: 'Vehicle', type: 'select', required: true, options: vehicleOptions },
      { name: 'parkingSlotId', label: 'Parking slot', type: 'select', required: true, options: slotOptions },
      { name: 'checkInTime', label: 'Check-in date', type: 'date', helpText: 'Leave blank on create to use the current time.' },
      { name: 'checkOutTime', label: 'Check-out date', type: 'date' },
      { name: 'status', label: 'Ticket status', type: 'select', required: true, options: () => TICKET_STATUS_OPTIONS, defaultValue: 'ACTIVE' },
      { name: 'paidStatus', label: 'Payment status', type: 'select', options: () => PAYMENT_OPTIONS, defaultValue: 'PENDING' },
      { name: 'hourlyRate', label: 'Hourly rate', type: 'number', step: 0.01 },
      { name: 'baseFee', label: 'Base fee', type: 'number', step: 0.01 },
      { name: 'excessFee', label: 'Excess fee', type: 'number', step: 0.01 },
      { name: 'tax', label: 'Tax', type: 'number', step: 0.01 },
      { name: 'totalFee', label: 'Total fee', type: 'number', step: 0.01 },
      { name: 'pendingDues', label: 'Pending dues', type: 'number', step: 0.01 },
      { name: 'createdBy', label: 'Created by' },
      { name: 'active', label: 'Active', type: 'checkbox', defaultValue: true },
      { name: 'deleted', label: 'Marked deleted', type: 'checkbox' },
    ],
    getFormValues: (record) => ({
      ticketId: stringValue(record?.ticketId),
      vehicleId: relationId(record?.vehicleDetails, 'vehicleId'),
      parkingSlotId: relationId(record?.parkingSlot, 'slotId'),
      checkInTime: formatDateForInput(record?.checkInTime),
      checkOutTime: formatDateForInput(record?.checkOutTime),
      status: stringValue(record?.status) || 'ACTIVE',
      paidStatus: stringValue(record?.paidStatus) || 'PENDING',
      hourlyRate: numericValue(record?.hourlyRate),
      baseFee: numericValue(record?.baseFee),
      excessFee: numericValue(record?.excessFee),
      tax: numericValue(record?.tax),
      totalFee: numericValue(record?.totalFee),
      pendingDues: numericValue(record?.pendingDues),
      createdBy: stringValue(record?.createdBy),
      active: Boolean(record?.active ?? record?.isActive),
      deleted: Boolean(record?.deleted ?? record?.isDeleted),
    }),
    toPayload: (values, _context, mode) => ({
      ...(mode === 'edit' ? { ticketId: stringValue(values.ticketId) } : {}),
      vehicleDetails: { vehicleId: stringValue(values.vehicleId) },
      parkingSlot: { slotId: stringValue(values.parkingSlotId) },
      checkInTime: formatDateForApi(values.checkInTime),
      checkOutTime: formatDateForApi(values.checkOutTime),
      status: stringValue(values.status),
      paidStatus: stringValue(values.paidStatus) || 'PENDING',
      hourlyRate: numericValue(values.hourlyRate),
      baseFee: numericValue(values.baseFee),
      excessFee: numericValue(values.excessFee),
      tax: numericValue(values.tax),
      totalFee: numericValue(values.totalFee),
      pendingDues: numericValue(values.pendingDues),
      createdBy: stringValue(values.createdBy),
      active: Boolean(values.active),
      isDeleted: Boolean(values.deleted),
    }),
  },
}

const RESOURCE_GROUPS: Array<{ label: string; keys: ResourceKey[] }> = [
  { label: 'People & access', keys: ['owners', 'ownerAadhaar', 'users', 'roles', 'privileges'] },
  { label: 'Owner records', keys: ['addresses', 'licenses'] },
  { label: 'Fleet', keys: ['vehicleTypes', 'vehicles'] },
  { label: 'Parking operations', keys: ['parkingSlots', 'parkingTickets'] },
]

function normalizeList(data: unknown): AnyRecord[] {
  if (Array.isArray(data)) return data.map(asRecord)
  const response = asRecord(data)
  if (Array.isArray(response.content)) return response.content.map(asRecord)
  if (data && typeof data === 'object') return [response]
  return []
}

function displayValue(value: unknown): string {
  if (value === null || value === undefined || value === '') return '—'
  if (Array.isArray(value)) return value.length ? `${value.length} linked records` : 'None'
  if (typeof value === 'object') return objectLabel(asRecord(value))
  if (typeof value === 'boolean') return value ? 'Yes' : 'No'
  return String(value).replaceAll('_', ' ')
}

function statusClass(value: unknown): string {
  const normalized = String(value ?? '').toLowerCase()
  if (['active', 'paid', 'true', 'yes', 'available', 'completed'].includes(normalized)) return 'status-chip status-chip--good'
  if (['pending', 'partially paid', 'false', 'no', 'cancelled', 'waived off'].includes(normalized)) return 'status-chip status-chip--warning'
  return 'status-chip'
}

function detailEntries(record: AnyRecord, prefix = '', depth = 0): Array<{ label: string; value: string }> {
  const entries: Array<{ label: string; value: string }> = []
  for (const [key, value] of Object.entries(record)) {
    if (key.toLowerCase().includes('password')) continue
    const label = prefix ? `${prefix} / ${key}` : key
    if (Array.isArray(value)) {
      if (value.length === 0) entries.push({ label, value: 'None' })
      else if (depth < 1 && value.every((item) => item && typeof item === 'object')) {
        entries.push({ label, value: `${value.length} linked records` })
      } else {
        entries.push({ label, value: value.map(displayValue).join(', ') })
      }
    } else if (value && typeof value === 'object') {
      entries.push({ label, value: objectLabel(asRecord(value)) })
      if (depth < 2) entries.push(...detailEntries(asRecord(value), label, depth + 1))
    } else {
      entries.push({ label, value: displayValue(value) })
    }
  }
  return entries
}

type AddressDraft = {
  flatNoHouseNo: string
  societyNameAptName: string
  areaName: string
  townOrCityName: string
  stateCode: string
  countryName: string
  pincode: string
}

const EMPTY_ADDRESS_DRAFT: AddressDraft = {
  flatNoHouseNo: '',
  societyNameAptName: '',
  areaName: '',
  townOrCityName: '',
  stateCode: '',
  countryName: 'India',
  pincode: '',
}

function addressSummary(record: AnyRecord): string {
  return [
    record.flatNoHouseNo,
    record.societyNameAptName,
    record.areaName,
    record.townOrCityName,
    record.stateCode,
    record.countryName,
    record.pincode,
  ].map(stringValue).filter(Boolean).join(', ')
}

function AddressInlineField({
  value,
  context,
  onChange,
}: {
  value: FormValue
  context: FormContext
  onChange: (value: FormValue) => void
}) {
  const [adding, setAdding] = useState(false)
  const [draft, setDraft] = useState<AddressDraft>(EMPTY_ADDRESS_DRAFT)
  const [createdAddress, setCreatedAddress] = useState<AnyRecord | null>(null)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const selectedId = stringValue(value)
  const linkedAddress = createdAddress?.addressId
    ? createdAddress
    : context.records.addresses.find((record) => stringValue(record.addressId) === selectedId)

  const updateDraft = (field: keyof AddressDraft, nextValue: string) => {
    setDraft((current) => ({ ...current, [field]: nextValue }))
  }

  const createAddress = async () => {
    if (Object.values(draft).some((field) => !field.trim())) {
      setError('Complete every address field before saving.')
      return
    }

    setSaving(true)
    setError('')
    try {
      const response = await client.post('/address/add_address', draft)
      const created = asRecord(response.data)
      const addressId = stringValue(created.addressId)
      if (!addressId) throw new Error('The address was created without an address ID.')
      setCreatedAddress(created)
      onChange(addressId)
      setDraft(EMPTY_ADDRESS_DRAFT)
      setAdding(false)
    } catch (requestError) {
      setError(apiError(requestError))
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="address-inline">
      {linkedAddress ? (
        <div className="address-inline__selected">
          <strong>{addressSummary(linkedAddress) || 'Linked address'}</strong>
          <span>{selectedId}</span>
        </div>
      ) : (
        <div className="address-inline__empty">No address linked yet.</div>
      )}
      <button className="button button--ghost address-inline__button" type="button" onClick={() => { setAdding((current) => !current); setError('') }}>
        <Plus size={15} /> {adding ? 'Close address form' : linkedAddress ? 'Add different address' : 'Add address'}
      </button>
      {adding ? (
        <div className="address-inline__form">
          <div className="address-inline__grid">
            <label>Flat / house number<input value={draft.flatNoHouseNo} onChange={(event) => updateDraft('flatNoHouseNo', event.target.value)} /></label>
            <label>Society / apartment<input value={draft.societyNameAptName} onChange={(event) => updateDraft('societyNameAptName', event.target.value)} /></label>
            <label>Area<input value={draft.areaName} onChange={(event) => updateDraft('areaName', event.target.value)} /></label>
            <label>Town / city<input value={draft.townOrCityName} onChange={(event) => updateDraft('townOrCityName', event.target.value)} /></label>
            <label>State<select value={draft.stateCode} onChange={(event) => updateDraft('stateCode', event.target.value)}><option value="">Select state</option>{STATE_OPTIONS.map((option) => <option value={option.value} key={option.value}>{option.label}</option>)}</select></label>
            <label>Country<input value={draft.countryName} onChange={(event) => updateDraft('countryName', event.target.value)} /></label>
            <label>Pincode<input value={draft.pincode} onChange={(event) => updateDraft('pincode', event.target.value)} /></label>
          </div>
          {error ? <div className="form-error address-inline__error">{error}</div> : null}
          <button className="button button--primary" type="button" onClick={() => void createAddress()} disabled={saving}>
            {saving ? <LoaderCircle size={15} className="spin" /> : <Check size={15} />}
            {saving ? 'Saving address…' : 'Save address'}
          </button>
        </div>
      ) : null}
    </div>
  )
}

function RecordDetails({ record }: { record: AnyRecord }) {
  const details = detailEntries(record)
  return (
    <div className="record-details">
      <div className="record-details__title">Complete record</div>
      <div className="record-details__grid">
        {details.map((detail) => (
          <div className="detail-item" key={`${detail.label}-${detail.value}`}>
            <span>{detail.label.replaceAll('_', ' ')}</span>
            <strong>{detail.value}</strong>
          </div>
        ))}
      </div>
    </div>
  )
}

function FieldControl({
  field,
  value,
  mode,
  context,
  onChange,
}: {
  field: FieldDefinition
  value: FormValue
  mode: 'create' | 'edit'
  context: FormContext
  onChange: (value: FormValue) => void
}) {
  const isReadOnly = mode === 'create' ? field.readOnlyOnCreate : field.readOnlyOnEdit
  const options = field.options?.(context) || []
  const inputId = `field-${field.name}`
  const required = field.required && !(mode === 'edit' && field.type === 'password')

  if (field.type === 'radio') {
    return (
      <div className="radio-grid">
        {options.map((option) => (
          <label className={`radio-card ${value === option.value ? 'radio-card--selected' : ''}`} key={option.value}>
            <input
              type="radio"
              name={field.name}
              value={option.value}
              checked={value === option.value}
              onChange={() => onChange(option.value)}
            />
            <span>{option.label}</span>
          </label>
        ))}
      </div>
    )
  }

  if (field.type === 'multiselect') {
    const selected = Array.isArray(value) ? value : []
    const allSelected = options.length > 0 && options.every((option) => selected.includes(option.value))
    return (
      <div className="multi-select-control">
        <div className="multi-select-toolbar">
          <span>{selected.length} of {options.length} selected</span>
          {options.length > 0 ? (
            <button className="multi-select-toolbar__action" type="button" onClick={() => onChange(allSelected ? [] : options.map((option) => option.value))}>
              {allSelected ? 'Clear all' : 'Select all'}
            </button>
          ) : null}
        </div>
        <div className="multi-select-grid">
          {options.length === 0 ? <div className="empty-inline">Create privileges first.</div> : null}
          {options.map((option) => {
            const checked = selected.includes(option.value)
            return (
              <label className={`multi-select-card ${checked ? 'multi-select-card--selected' : ''}`} key={option.value}>
                <input
                  type="checkbox"
                  checked={checked}
                  onChange={() => onChange(checked ? selected.filter((item) => item !== option.value) : [...selected, option.value])}
                />
                <span>{option.label}</span>
                {option.hint ? <small>{option.hint}</small> : null}
              </label>
            )
          })}
        </div>
      </div>
    )
  }

  if (field.type === 'address') {
    return <AddressInlineField value={value} context={context} onChange={onChange} />
  }

  if (field.type === 'checkbox') {
    return (
      <label className="toggle-control">
        <input type="checkbox" checked={Boolean(value)} onChange={(event) => onChange(event.target.checked)} />
        <span className="toggle-control__track"><span /></span>
        <span>{value ? 'Enabled' : 'Disabled'}</span>
      </label>
    )
  }

  if (field.type === 'select') {
    return (
      <select
        id={inputId}
        value={String(value ?? '')}
        onChange={(event) => onChange(event.target.value)}
        disabled={isReadOnly}
        required={required}
      >
        <option value="">Select {field.label.toLowerCase()}</option>
        {options.map((option) => (
          <option value={option.value} key={option.value}>
            {option.label}{option.hint ? ` · ${option.hint}` : ''}
          </option>
        ))}
      </select>
    )
  }

  const inputType = field.type || 'text'
  return (
    <input
      id={inputId}
      type={inputType}
      value={String(value ?? '')}
      onChange={(event) => onChange(inputType === 'number' ? event.target.value : event.target.value)}
      placeholder={field.placeholder}
      readOnly={isReadOnly}
      required={required}
      min={field.min}
      step={field.step}
      list={field.name === 'typeName' ? 'vehicle-type-presets' : undefined}
    />
  )
}

function RecordFormModal({
  definition,
  context,
  state,
  onClose,
  onSave,
}: {
  definition: ResourceDefinition
  context: FormContext
  state: ModalState
  onClose: () => void
  onSave: (values: FormValues) => Promise<void>
}) {
  const [values, setValues] = useState<FormValues>(() =>
    state.record ? definition.getFormValues(state.record) : defaultForm(definition.fields),
  )
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setError('')
    const missingField = definition.fields.find((field) => {
      if (!field.required || (field.visible && !field.visible(state.mode))) return false
      const value = values[field.name]
      return Array.isArray(value) ? value.length === 0 : !stringValue(value).trim()
    })
    if (missingField) {
      setError(`${missingField.label} is required.`)
      return
    }

    setSaving(true)
    try {
      await onSave(values)
    } catch (saveError) {
      setError(apiError(saveError))
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="modal-backdrop" role="presentation" onMouseDown={(event) => event.target === event.currentTarget && onClose()}>
      <section className="record-modal" role="dialog" aria-modal="true" aria-labelledby="record-modal-title">
        <div className="record-modal__header">
          <div>
            <p className="eyebrow">{state.mode === 'create' ? 'New record' : 'Edit record'}</p>
            <h2 id="record-modal-title">{state.mode === 'create' ? `Add ${definition.label}` : `Update ${definition.label}`}</h2>
          </div>
          <button className="icon-button" type="button" onClick={onClose} aria-label="Close form">
            <X size={18} />
          </button>
        </div>
        <form onSubmit={submit}>
          <div className="form-grid">
            {definition.fields.map((field) => {
              if (field.visible && !field.visible(state.mode)) return null
              const value = values[field.name] ?? (field.type === 'checkbox' ? false : field.type === 'multiselect' ? [] : '')
              return (
                <div className={`form-field ${field.fullWidth || field.type === 'radio' || field.type === 'multiselect' ? 'form-field--full' : ''}`} key={field.name}>
                  <label htmlFor={`field-${field.name}`}>
                    {field.label}{field.required ? <span className="required-mark">*</span> : null}
                  </label>
                  <FieldControl
                    field={field}
                    value={value}
                    mode={state.mode}
                    context={context}
                    onChange={(nextValue) => setValues((current) => ({ ...current, [field.name]: nextValue }))}
                  />
                  {field.helpText ? <small className="field-help">{field.helpText}</small> : null}
                </div>
              )
            })}
          </div>
          {definition.key === 'vehicleTypes' ? (
            <datalist id="vehicle-type-presets">
              {VEHICLE_TYPE_PRESETS.map((preset) => <option value={preset} key={preset} />)}
            </datalist>
          ) : null}
          {error ? <div className="form-error">{error}</div> : null}
          <div className="record-modal__footer">
            <button className="button button--ghost" type="button" onClick={onClose} disabled={saving}>Cancel</button>
            <button className="button button--primary" type="submit" disabled={saving}>
              {saving ? <LoaderCircle size={16} className="spin" /> : <Check size={16} />}
              {saving ? 'Saving…' : state.mode === 'create' ? 'Create record' : 'Save changes'}
            </button>
          </div>
        </form>
      </section>
    </div>
  )
}

function Pagination({ page, totalPages, onChange }: { page: number; totalPages: number; onChange: (page: number) => void }) {
  if (totalPages <= 1) return null
  return (
    <div className="pagination" aria-label="Pagination">
      <button className="icon-button icon-button--small" type="button" onClick={() => onChange(page - 1)} disabled={page === 1} aria-label="Previous page">
        <ChevronLeft size={16} />
      </button>
      <span>Page <strong>{page}</strong> of <strong>{totalPages}</strong></span>
      <button className="icon-button icon-button--small" type="button" onClick={() => onChange(page + 1)} disabled={page === totalPages} aria-label="Next page">
        <ChevronRight size={16} />
      </button>
    </div>
  )
}

export default function Records({ initialResource = 'owners' }: { initialResource?: ResourceKey }) {
  const [activeKey, setActiveKey] = useState<ResourceKey>(initialResource)
  const [records, setRecords] = useState<Record<ResourceKey, AnyRecord[]>>(emptyRecords)
  const [loading, setLoading] = useState<Record<ResourceKey, boolean>>({} as Record<ResourceKey, boolean>)
  const [errors, setErrors] = useState<Record<ResourceKey, string>>({} as Record<ResourceKey, string>)
  const [query, setQuery] = useState('')
  const [page, setPage] = useState(1)
  const [expandedId, setExpandedId] = useState('')
  const [modal, setModal] = useState<ModalState | null>(null)
  const [notice, setNotice] = useState<Notice | null>(null)

  const loadResource = useCallback(async (key: ResourceKey) => {
    const definition = RESOURCE_DEFINITIONS[key]
    setLoading((current) => ({ ...current, [key]: true }))
    setErrors((current) => ({ ...current, [key]: '' }))
    try {
      const response = await client.get(definition.listPath, definition.paged ? { params: { page: 0, size: 1000 } } : undefined)
      setRecords((current) => ({ ...current, [key]: normalizeList(response.data) }))
    } catch (error) {
      setErrors((current) => ({ ...current, [key]: apiError(error) }))
    } finally {
      setLoading((current) => ({ ...current, [key]: false }))
    }
  }, [])

  const reloadAll = useCallback(async () => {
    await Promise.allSettled(RESOURCE_KEYS.map((key) => loadResource(key)))
  }, [loadResource])

  useEffect(() => {
    void reloadAll()
  }, [reloadAll])

  const context: FormContext = useMemo(() => ({ records }), [records])
  const definition = RESOURCE_DEFINITIONS[activeKey]
  const activeItems = records[activeKey]
  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase()
    if (!normalized) return activeItems
    return activeItems.filter((record) => JSON.stringify(record).toLowerCase().includes(normalized))
  }, [activeItems, query])
  const totalPages = Math.max(1, Math.ceil(filteredItems.length / PAGE_SIZE))
  const visibleItems = filteredItems.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE)
  const isLoading = Boolean(loading[activeKey])

  const saveRecord = async (values: FormValues) => {
    if (!modal) return
    const payload = definition.toPayload(values, context, modal.mode)
    if (modal.mode === 'create') {
      await client.post(definition.createPath, payload)
      setNotice({ tone: 'success', message: `${definition.label} created.` })
    } else {
      const id = definition.id(modal.record || {})
      await client.put(definition.updatePath(id), payload)
      setNotice({ tone: 'success', message: `${definition.label} updated.` })
    }
    setModal(null)
    await reloadAll()
  }

  const deleteRecord = async (record: AnyRecord) => {
    const id = definition.id(record)
    if (!id || !window.confirm(`Delete this ${definition.label.toLowerCase()}?`)) return
    try {
      await client.delete(definition.deletePath(id))
      setNotice({ tone: 'success', message: `${definition.label} deleted.` })
      await loadResource(activeKey)
    } catch (error) {
      setNotice({ tone: 'error', message: apiError(error) })
    }
  }

  const changeResource = (key: ResourceKey) => {
    setActiveKey(key)
    setQuery('')
    setPage(1)
    setExpandedId('')
  }

  return (
    <div className="records-page">
      <aside className="records-sidebar">
        <div className="records-sidebar__intro">
          <div className="records-sidebar__icon"><Database size={18} /></div>
          <div>
            <p className="eyebrow">Data registry</p>
            <h2>Parking tables</h2>
          </div>
        </div>
        <p className="records-sidebar__copy">One workspace for the owner, fleet, access, and parking records behind the lot.</p>
        <nav className="resource-nav" aria-label="Parking data tables">
          {RESOURCE_GROUPS.map((group) => (
            <div className="resource-nav__group" key={group.label}>
              <span className="resource-nav__label">{group.label}</span>
              {group.keys.map((key) => {
                const item = RESOURCE_DEFINITIONS[key]
                const Icon = item.icon
                return (
                  <button className={`resource-nav__item ${key === activeKey ? 'resource-nav__item--active' : ''}`} type="button" onClick={() => changeResource(key)} key={key}>
                    <Icon size={16} />
                    <span>{item.pluralLabel}</span>
                    <span className="resource-nav__count">{records[key].length}</span>
                  </button>
                )
              })}
            </div>
          ))}
        </nav>
      </aside>

      <section className="records-content">
        <div className="records-heading">
          <div>
            <p className="eyebrow">{definition.eyebrow} / table view</p>
            <h1>{definition.pluralLabel}</h1>
            <p className="records-heading__description">{definition.description}</p>
          </div>
          <div className="records-heading__actions">
            <button className="button button--ghost" type="button" onClick={() => void loadResource(activeKey)} disabled={isLoading}>
              <RefreshCw size={16} className={isLoading ? 'spin' : ''} /> Refresh
            </button>
            <button className="button button--primary" type="button" onClick={() => setModal({ mode: 'create' })}>
              <Plus size={17} /> Add {definition.label}
            </button>
          </div>
        </div>

        <div className="records-toolbar">
          <label className="search-box">
            <Search size={17} />
            <input value={query} onChange={(event) => { setQuery(event.target.value); setPage(1); setExpandedId('') }} placeholder={`Search ${definition.pluralLabel.toLowerCase()}…`} />
          </label>
          <div className="records-toolbar__meta">
            <span className="live-dot" />
            {filteredItems.length} shown · {activeItems.length} total
          </div>
        </div>

        {notice ? (
          <div className={`notice notice--${notice.tone}`}>
            <span>{notice.message}</span>
            <button className="icon-button icon-button--small" type="button" onClick={() => setNotice(null)} aria-label="Dismiss notification"><X size={14} /></button>
          </div>
        ) : null}

        {errors[activeKey] ? (
          <div className="error-panel">
            <div><strong>Could not load this table.</strong><span>{errors[activeKey]}</span></div>
            <button className="button button--ghost" type="button" onClick={() => void loadResource(activeKey)}>Try again</button>
          </div>
        ) : null}

        <div className="records-card">
          {isLoading && activeItems.length === 0 ? (
            <div className="loading-state"><LoaderCircle size={24} className="spin" /><span>Loading {definition.pluralLabel.toLowerCase()}…</span></div>
          ) : visibleItems.length === 0 ? (
            <div className="empty-state">
              <div className="empty-state__icon"><Database size={20} /></div>
              <h3>{query ? 'No matching records' : `No ${definition.pluralLabel.toLowerCase()} yet`}</h3>
              <p>{query ? 'Try a different search term.' : `Create the first ${definition.label.toLowerCase()} to populate this table.`}</p>
              {!query ? <button className="button button--primary" type="button" onClick={() => setModal({ mode: 'create' })}><Plus size={16} /> Add record</button> : null}
            </div>
          ) : (
            <div className="record-table">
              <div className="record-table__head" style={{ gridTemplateColumns: `repeat(${definition.columns.length}, minmax(0, 1fr)) 92px` }}>
                {definition.columns.map((column) => <span key={column.path}>{column.label}</span>)}
                <span>Actions</span>
              </div>
              {visibleItems.map((record) => {
                const recordId = definition.id(record)
                const expanded = expandedId === recordId
                return (
                  <div className={`record-entry ${expanded ? 'record-entry--expanded' : ''}`} key={recordId || JSON.stringify(record)}>
                    <div
                      className="record-table__row"
                      style={{ gridTemplateColumns: `repeat(${definition.columns.length}, minmax(0, 1fr)) 92px` }}
                      role="button"
                      tabIndex={0}
                      onClick={() => setExpandedId(expanded ? '' : recordId)}
                      onKeyDown={(event) => {
                        if (event.key === 'Enter' || event.key === ' ') setExpandedId(expanded ? '' : recordId)
                      }}
                    >
                      {definition.columns.map((column) => {
                        const value = valueAt(record, column.path)
                        const textValue = displayValue(value)
                        const isStatus = ['status', 'paidStatus', 'available', 'active'].includes(column.path.split('.').at(-1) || '')
                        return (
                          <span className={`record-cell record-cell--${column.tone || 'plain'}`} key={column.path}>
                            {isStatus ? <span className={statusClass(textValue)}>{textValue}</span> : textValue}
                          </span>
                        )
                      })}
                      <span className="record-actions" onClick={(event) => event.stopPropagation()}>
                        <button className="icon-button icon-button--small" type="button" onClick={() => setModal({ mode: 'edit', record })} aria-label={`Edit ${definition.label}`} title="Edit">
                          <Edit3 size={15} />
                        </button>
                        <button className="icon-button icon-button--small icon-button--danger" type="button" onClick={() => void deleteRecord(record)} aria-label={`Delete ${definition.label}`} title="Delete">
                          <Trash2 size={15} />
                        </button>
                        {expanded ? <ChevronUp size={16} className="row-chevron" /> : <ChevronDown size={16} className="row-chevron" />}
                      </span>
                    </div>
                    {expanded ? <RecordDetails record={record} /> : null}
                  </div>
                )
              })}
            </div>
          )}
        </div>

        <div className="records-footer">
          <span>Click any row to reveal every stored field and linked record.</span>
          <Pagination page={Math.min(page, totalPages)} totalPages={totalPages} onChange={setPage} />
        </div>
      </section>

      {modal ? <RecordFormModal key={`${activeKey}-${modal.mode}-${definition.id(modal.record || {})}`} definition={definition} context={context} state={modal} onClose={() => setModal(null)} onSave={saveRecord} /> : null}
    </div>
  )
}
