import React, { useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  TextField,
  Button,
  Typography,
  Alert,
  CircularProgress,
  Link,
  Container,
  MenuItem,
} from '@mui/material';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';

interface RegisterFormData {
  name: string;
  email: string;
  district: string;
  className: string;
  password: string;
  confirmPassword: string;
  otp: string;
}

interface RegisterProps {
  onSwitchToLogin: () => void;
}

const classOptions = [
  '10',
  '11',
  '12',
  'Graduate',
  'Post Graduate'
];

const Register: React.FC<RegisterProps> = ({ onSwitchToLogin }) => {
  const [formData, setFormData] = useState<RegisterFormData>({
    name: '',
    email: '',
    district: '',
    className: '',
    password: '',
    confirmPassword: '',
    otp: ''
  });
  const [loading, setLoading] = useState(false);
  const [otpLoading, setOtpLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [step, setStep] = useState<'details' | 'otp'>('details');
  const navigate = useNavigate();
  const { sendRegistrationOtp, register } = useAuth();

  const handleInputChange = (field: string, value: string) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleSendOtp = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    // Validate required fields for step 1
    if (!formData.name || !formData.email || !formData.password || !formData.district || !formData.className) {
      setError('Please fill in all required fields');
      return;
    }

    // Validate password confirmation
    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    // Validate email format
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) {
      setError('Please enter a valid email address');
      return;
    }

    // Validate password length
    if (formData.password.length < 6) {
      setError('Password must be at least 6 characters long');
      return;
    }

    setOtpLoading(true);
    const result = await sendRegistrationOtp(formData.email);
    setOtpLoading(false);

    if (result.success) {
      setSuccess('OTP sent successfully! Please check your email.');
      setStep('otp');
    } else {
      setError(result.message || 'Failed to send OTP. Please try again.');
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!formData.otp) {
      setError('Please enter the OTP');
      return;
    }

    const result = await register(
      formData.name,
      formData.email,
      formData.password,
      formData.district,
      formData.className,
      formData.otp
    );

    if (result.success) {
      navigate('/dashboard');
    } else {
      setError(result.message || 'Registration failed. Please try again.');
    }
  };

  const handleBackToDetails = () => {
    setStep('details');
    setFormData((prev: RegisterFormData) => ({ ...prev, otp: '' }));
    setError('');
    setSuccess('');
  };  return (
    <Container maxWidth="sm">
      <Box
        display="flex"
        flexDirection="column"
        alignItems="center"
        justifyContent="center"
        minHeight="100vh"
        py={4}
      >
        <Card sx={{ width: '100%', maxWidth: 400 }}>
          <CardContent sx={{ p: 4 }}>
            <Typography variant="h4" component="h1" gutterBottom align="center">
              {step === 'details' ? 'Register' : 'Verify Email'}
            </Typography>
            <Typography variant="body2" color="text.secondary" align="center" sx={{ mb: 3 }}>
              {step === 'details' 
                ? 'Create your account to get started' 
                : `Enter the OTP sent to ${formData.email}`
              }
            </Typography>

            {error && (
              <Alert severity="error" sx={{ mb: 2 }}>
                {error}
              </Alert>
            )}

            {success && (
              <Alert severity="success" sx={{ mb: 2 }}>
                {success}
              </Alert>
            )}

            {step === 'details' ? (
              <form onSubmit={handleSendOtp}>
                <TextField
                  fullWidth
                  label="Full Name"
                  value={formData.name}
                  onChange={(e) => handleInputChange('name', e.target.value)}
                  margin="normal"
                  required
                  autoComplete="name"
                  placeholder="Enter your full name"
                />
                <TextField
                  fullWidth
                  label="Email"
                  value={formData.email}
                  onChange={(e) => handleInputChange('email', e.target.value)}
                  margin="normal"
                  required
                  autoComplete="email"
                  placeholder="Enter your email address"
                />
                <TextField
                  fullWidth
                  label="District"
                  value={formData.district}
                  onChange={(e) => handleInputChange('district', e.target.value)}
                  margin="normal"
                  required
                  placeholder="Enter your district"
                />
                <TextField
                  fullWidth
                  select
                  label="Class"
                  value={formData.className}
                  onChange={(e) => handleInputChange('className', e.target.value)}
                  margin="normal"
                  required
                >
                  {classOptions.map((option) => (
                    <MenuItem key={option} value={option}>
                      {option}
                    </MenuItem>
                  ))}
                </TextField>
                <TextField
                  fullWidth
                  label="Password"
                  type="password"
                  value={formData.password}
                  onChange={(e) => handleInputChange('password', e.target.value)}
                  margin="normal"
                  required
                  autoComplete="new-password"
                  placeholder="Enter your password"
                />
                <TextField
                  fullWidth
                  label="Confirm Password"
                  type="password"
                  value={formData.confirmPassword}
                  onChange={(e) => handleInputChange('confirmPassword', e.target.value)}
                  margin="normal"
                  required
                  autoComplete="new-password"
                  placeholder="Confirm your password"
                />
                <Button
                  type="submit"
                  fullWidth
                  variant="contained"
                  size="large"
                  disabled={otpLoading}
                  sx={{ mt: 3, mb: 2 }}
                >
                  {otpLoading ? <CircularProgress size={24} /> : 'Send OTP'}
                </Button>
              </form>
            ) : (
              <form onSubmit={handleSubmit}>
                <TextField
                  fullWidth
                  label="Enter OTP"
                  value={formData.otp}
                  onChange={(e) => handleInputChange('otp', e.target.value)}
                  margin="normal"
                  required
                  placeholder="Enter the 6-digit OTP"
                  inputProps={{ maxLength: 6 }}
                />
                <Button
                  type="submit"
                  fullWidth
                  variant="contained"
                  size="large"
                  disabled={loading}
                  sx={{ mt: 3, mb: 2 }}
                >
                  {loading ? <CircularProgress size={24} /> : 'Complete Registration'}
                </Button>
                <Button
                  fullWidth
                  variant="outlined"
                  size="large"
                  onClick={handleBackToDetails}
                  sx={{ mb: 2 }}
                >
                  Back to Details
                </Button>
              </form>
            )}

            <Box textAlign="center">
              <Typography variant="body2">
                Already have an account?{' '}
                <Link
                  component="button"
                  variant="body2"
                  onClick={(e) => {
                    e.preventDefault();
                    onSwitchToLogin();
                  }}
                >
                  Login here
                </Link>
              </Typography>
            </Box>
          </CardContent>
        </Card>
      </Box>
    </Container>
  );
};

export default Register;