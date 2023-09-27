import React from 'react';
import Index from '@/app/page'
import { render } from "@testing-library/react";

test('renders without errors', () => {
    render(<Index/>);
});
