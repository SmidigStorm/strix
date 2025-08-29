import React from 'react'
import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import { userEvent } from '@testing-library/user-event'
import { RoleProvider, useRole, type Role } from '../contexts/RoleContext'

// Test component to access the role context
function TestComponent() {
  const { selectedRole, setSelectedRole, hasPermission } = useRole()

  return (
    <div>
      <span data-testid="current-role">{selectedRole}</span>
      <button
        onClick={() => setSelectedRole('Søker' as Role)}
        data-testid="set-soker"
      >
        Set Søker
      </button>
      <button
        onClick={() => setSelectedRole('Opptaksleder' as Role)}
        data-testid="set-opptaksleder"
      >
        Set Opptaksleder
      </button>
      <button
        onClick={() => setSelectedRole('Administrator' as Role)}
        data-testid="set-administrator"
      >
        Set Administrator
      </button>
      
      {/* Permission indicators */}
      <span data-testid="view-orgs">
        {hasPermission('VIEW_ORGANISATIONS') ? 'can-view' : 'cannot-view'}
      </span>
      <span data-testid="create-orgs">
        {hasPermission('CREATE_ORGANISATION') ? 'can-create' : 'cannot-create'}
      </span>
      <span data-testid="edit-orgs">
        {hasPermission('EDIT_ORGANISATION') ? 'can-edit' : 'cannot-edit'}
      </span>
      <span data-testid="delete-orgs">
        {hasPermission('DELETE_ORGANISATION') ? 'can-delete' : 'cannot-delete'}
      </span>
      <span data-testid="admin-orgs">
        {hasPermission('ADMIN_ORGANISATIONS') ? 'full-admin' : 'no-admin'}
      </span>
    </div>
  )
}

describe('RoleContext', () => {
  it('should start with Administrator as default role', () => {
    render(
      <RoleProvider>
        <TestComponent />
      </RoleProvider>
    )

    expect(screen.getByTestId('current-role')).toHaveTextContent('Administrator')
  })

  it('should allow changing roles', async () => {
    const user = userEvent.setup()
    
    render(
      <RoleProvider>
        <TestComponent />
      </RoleProvider>
    )

    // Initially Administrator
    expect(screen.getByTestId('current-role')).toHaveTextContent('Administrator')

    // Change to Søker
    await user.click(screen.getByTestId('set-soker'))
    expect(screen.getByTestId('current-role')).toHaveTextContent('Søker')

    // Change to Opptaksleder
    await user.click(screen.getByTestId('set-opptaksleder'))
    expect(screen.getByTestId('current-role')).toHaveTextContent('Opptaksleder')

    // Change back to Administrator
    await user.click(screen.getByTestId('set-administrator'))
    expect(screen.getByTestId('current-role')).toHaveTextContent('Administrator')
  })

  describe('Administrator permissions', () => {
    it('should have full permissions for Administrator role', () => {
      render(
        <RoleProvider>
          <TestComponent />
        </RoleProvider>
      )

      // Administrator should have all permissions
      expect(screen.getByTestId('view-orgs')).toHaveTextContent('can-view')
      expect(screen.getByTestId('create-orgs')).toHaveTextContent('can-create')
      expect(screen.getByTestId('edit-orgs')).toHaveTextContent('can-edit')
      expect(screen.getByTestId('delete-orgs')).toHaveTextContent('can-delete')
      expect(screen.getByTestId('admin-orgs')).toHaveTextContent('full-admin')
    })
  })

  describe('Opptaksleder permissions', () => {
    it('should have limited permissions for Opptaksleder role', async () => {
      const user = userEvent.setup()
      
      render(
        <RoleProvider>
          <TestComponent />
        </RoleProvider>
      )

      // Change to Opptaksleder
      await user.click(screen.getByTestId('set-opptaksleder'))

      // Opptaksleder should have limited permissions
      expect(screen.getByTestId('view-orgs')).toHaveTextContent('can-view')
      expect(screen.getByTestId('create-orgs')).toHaveTextContent('cannot-create')
      expect(screen.getByTestId('edit-orgs')).toHaveTextContent('cannot-edit')
      expect(screen.getByTestId('delete-orgs')).toHaveTextContent('cannot-delete')
      expect(screen.getByTestId('admin-orgs')).toHaveTextContent('no-admin')
    })
  })

  describe('Søknadsbehandler permissions', () => {
    it('should have view-only permissions for Søknadsbehandler role', async () => {
      const TestComponentWithSoknadsbehandler = () => {
        const { selectedRole, setSelectedRole, hasPermission } = useRole()

        // Force set to Søknadsbehandler for this test
        React.useEffect(() => {
          setSelectedRole('Søknadsbehandler' as Role)
        }, [setSelectedRole])

        return (
          <div>
            <span data-testid="current-role">{selectedRole}</span>
            <span data-testid="view-orgs">
              {hasPermission('VIEW_ORGANISATIONS') ? 'can-view' : 'cannot-view'}
            </span>
            <span data-testid="create-orgs">
              {hasPermission('CREATE_ORGANISATION') ? 'can-create' : 'cannot-create'}
            </span>
            <span data-testid="edit-orgs">
              {hasPermission('EDIT_ORGANISATION') ? 'can-edit' : 'cannot-edit'}
            </span>
            <span data-testid="delete-orgs">
              {hasPermission('DELETE_ORGANISATION') ? 'can-delete' : 'cannot-delete'}
            </span>
            <span data-testid="admin-orgs">
              {hasPermission('ADMIN_ORGANISATIONS') ? 'full-admin' : 'no-admin'}
            </span>
          </div>
        )
      }

      render(
        <RoleProvider>
          <TestComponentWithSoknadsbehandler />
        </RoleProvider>
      )

      // Wait for role to be set
      await screen.findByText('Søknadsbehandler')

      // Søknadsbehandler should have view-only permissions
      expect(screen.getByTestId('view-orgs')).toHaveTextContent('can-view')
      expect(screen.getByTestId('create-orgs')).toHaveTextContent('cannot-create')
      expect(screen.getByTestId('edit-orgs')).toHaveTextContent('cannot-edit')
      expect(screen.getByTestId('delete-orgs')).toHaveTextContent('cannot-delete')
      expect(screen.getByTestId('admin-orgs')).toHaveTextContent('no-admin')
    })
  })

  describe('Søker permissions', () => {
    it('should have view-only permissions for Søker role', async () => {
      const user = userEvent.setup()
      
      render(
        <RoleProvider>
          <TestComponent />
        </RoleProvider>
      )

      // Change to Søker
      await user.click(screen.getByTestId('set-soker'))

      // Søker should have view-only permissions
      expect(screen.getByTestId('view-orgs')).toHaveTextContent('can-view')
      expect(screen.getByTestId('create-orgs')).toHaveTextContent('cannot-create')
      expect(screen.getByTestId('edit-orgs')).toHaveTextContent('cannot-edit')
      expect(screen.getByTestId('delete-orgs')).toHaveTextContent('cannot-delete')
      expect(screen.getByTestId('admin-orgs')).toHaveTextContent('no-admin')
    })
  })

  describe('Permission system', () => {
    it('should return false for unknown permissions', () => {
      render(
        <RoleProvider>
          <TestComponent />
        </RoleProvider>
      )

      const TestUnknownPermission = () => {
        const { hasPermission } = useRole()
        return (
          <span data-testid="unknown-permission">
            {hasPermission('UNKNOWN_PERMISSION') ? 'allowed' : 'denied'}
          </span>
        )
      }

      render(
        <RoleProvider>
          <TestUnknownPermission />
        </RoleProvider>
      )

      expect(screen.getByTestId('unknown-permission')).toHaveTextContent('denied')
    })

    it('should throw error when used outside provider', () => {
      // This should throw an error
      expect(() => {
        render(<TestComponent />)
      }).toThrow('useRole must be used within a RoleProvider')
    })
  })
})