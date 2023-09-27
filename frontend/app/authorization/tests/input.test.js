import { fireEvent, render, screen } from '@testing-library/react';
import { Input } from '@/app/authorization/input';
import '@testing-library/jest-dom/extend-expect';

describe('Input component', () => {
    test('renders input field correctly', () => {
        render(
            <Input
                type="text"
                placeholder="Your placeholder"
                className="your-classname"
                onChange={() => {
                }}
            />
        );

        const inputField = screen.getByPlaceholderText('Your placeholder');
        expect(inputField).toBeInTheDocument();
    });

    test('updates on change', () => {
        const handleChange = jest.fn();
        render(
            <Input
                type="text"
                placeholder="Your placeholder"
                className="your-classname"
                onChange={handleChange}
            />
        );

        const inputField = screen.getByPlaceholderText('Your placeholder');
        fireEvent.change(inputField, { target: { value: 'My input' } });

        expect(handleChange).toHaveBeenCalledWith('My input');
    });

    test('password field hides and shows text', () => {
        render(
            <Input
                type="password"
                placeholder="Your password"
                className="your-classname"
                onChange={() => {
                }}
            />
        );

        const passwordField = screen.getByPlaceholderText('Your password');
        const toggleIcon = screen.getByRole('button'); // Use `getByTestId` to locate the toggle icon

        // Initially, text should be hidden
        fireEvent.change(passwordField, { target: { value: 'My password' } });
        expect(passwordField).toHaveValue('My password');
        expect(passwordField).toHaveAttribute('type', 'password');

        // After clicking the toggle, text should be visible
        fireEvent.click(toggleIcon);
        expect(passwordField).toHaveAttribute('type', 'text');
    });
});