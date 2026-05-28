import { useState } from 'react';
import { X, Mail, Lock, User, Shield, ArrowLeft, Check } from 'lucide-react';

interface AuthModalProps {
  onClose: () => void;
  onLogin: (email: string, password: string) => void;
  onRegister: (username: string, email: string, password: string) => void;
}

export function AuthModal({ onClose, onLogin, onRegister }: AuthModalProps) {
  const [mode, setMode] = useState<'login' | 'register' | 'forgotPassword'>('login');
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [resetEmailSent, setResetEmailSent] = useState(false);
  const [passwordFocused, setPasswordFocused] = useState(false);

  const passwordRequirements = [
    { id: 1, text: 'At least 8 characters', test: (pwd: string) => pwd.length >= 8 },
    { id: 2, text: 'One uppercase letter', test: (pwd: string) => /[A-Z]/.test(pwd) },
    { id: 3, text: 'One lowercase letter', test: (pwd: string) => /[a-z]/.test(pwd) },
    { id: 4, text: 'One number', test: (pwd: string) => /[0-9]/.test(pwd) },
    { id: 5, text: 'One special character', test: (pwd: string) => /[^A-Za-z0-9]/.test(pwd) },
  ];

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (mode === 'login') {
      onLogin(email, password);
    } else if (mode === 'register') {
      onRegister(username, email, password);
    } else if (mode === 'forgotPassword') {
      // In production, this would call your Java backend API
      console.log('Password reset requested for:', email);
      setResetEmailSent(true);
    }
  };

  const handleBackToLogin = () => {
    setMode('login');
    setResetEmailSent(false);
    setEmail('');
  };

  return (
    <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-50">
      <div className="bg-[rgba(30,41,59,0.7)] backdrop-blur-xl border border-white/10 rounded-2xl max-w-md w-full p-8 relative shadow-2xl">
        <button
          onClick={onClose}
          className="absolute top-4 right-4 p-2 text-gray-400 hover:text-white rounded-lg hover:bg-white/10 transition-colors"
        >
          <X className="w-5 h-5" />
        </button>

        {mode === 'forgotPassword' ? (
          // Forgot Password View
          <>
            <button
              onClick={handleBackToLogin}
              className="flex items-center space-x-2 text-gray-400 hover:text-white mb-6 transition-colors"
            >
              <ArrowLeft className="w-4 h-4" />
              <span className="text-sm">Back to login</span>
            </button>

            <div className="text-center mb-8">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-[#646cff] rounded-2xl mb-4 shadow-lg shadow-[#646cff]/30">
                <Mail className="w-8 h-8 text-white" />
              </div>
              <h2 className="text-2xl mb-2 text-white">
                Forgot Password?
              </h2>
              <p className="text-gray-400">
                {resetEmailSent 
                  ? "Check your email for reset instructions"
                  : "Enter your email to receive a password reset link"}
              </p>
            </div>

            {!resetEmailSent ? (
              <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                  <label className="flex items-center gap-1 mb-2 text-gray-300 text-sm">
                    Email Address
                    <span className="text-red-400">*</span>
                  </label>
                  <div className="relative">
                    <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                    <input
                      type="email"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      className="w-full pl-10 pr-4 py-3 bg-[rgba(15,23,42,0.6)] border border-white/10 rounded-xl text-white placeholder-gray-500 focus:outline-none focus:border-[#646cff] focus:ring-4 focus:ring-[#646cff]/20 transition-all"
                      placeholder="Enter your email"
                      required
                    />
                  </div>
                </div>

                <button
                  type="submit"
                  className="w-full py-3 bg-[#646cff] text-white rounded-xl hover:bg-[#535bf2] transition-all shadow-lg shadow-[#646cff]/30"
                >
                  Send Reset Link
                </button>
              </form>
            ) : (
              <div className="space-y-4">
                <div className="p-4 bg-emerald-500/10 border border-emerald-500/30 rounded-xl">
                  <p className="text-sm text-emerald-400 text-center">
                    ✓ Password reset email sent to <span className="font-mono">{email}</span>
                  </p>
                </div>
                <button
                  onClick={handleBackToLogin}
                  className="w-full py-3 bg-[#646cff] text-white rounded-xl hover:bg-[#535bf2] transition-all shadow-lg shadow-[#646cff]/30"
                >
                  Back to Login
                </button>
              </div>
            )}
          </>
        ) : (
          // Login/Register View
          <>
            <div className="text-center mb-8">
              <div className="inline-flex items-center justify-center w-16 h-16 bg-[#646cff] rounded-2xl mb-4 shadow-lg shadow-[#646cff]/30">
                <Shield className="w-8 h-8 text-white" />
              </div>
              <h2 className="text-2xl mb-2 text-white">
                {mode === 'login' ? 'Welcome Back' : 'Create Account'}
              </h2>
              <p className="text-gray-400">
                {mode === 'login'
                  ? 'Sign in to access your account'
                  : 'Join us and start winning today'}
              </p>
            </div>

            <form onSubmit={handleSubmit} className="space-y-4">
              {mode === 'register' && (
                <div>
                  <label className="flex items-center gap-1 mb-2 text-gray-300 text-sm">
                    Username
                    <span className="text-red-400">*</span>
                  </label>
                  <div className="relative">
                    <User className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                    <input
                      type="text"
                      value={username}
                      onChange={(e) => setUsername(e.target.value)}
                      className="w-full pl-10 pr-4 py-3 bg-[rgba(15,23,42,0.6)] border border-white/10 rounded-xl text-white placeholder-gray-500 focus:outline-none focus:border-[#646cff] focus:ring-4 focus:ring-[#646cff]/20 transition-all"
                      placeholder="Enter your username"
                      required
                    />
                  </div>
                </div>
              )}

              <div>
                <label className="flex items-center gap-1 mb-2 text-gray-300 text-sm">
                  Email
                  <span className="text-red-400">*</span>
                </label>
                <div className="relative">
                  <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                  <input
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="w-full pl-10 pr-4 py-3 bg-[rgba(15,23,42,0.6)] border border-white/10 rounded-xl text-white placeholder-gray-500 focus:outline-none focus:border-[#646cff] focus:ring-4 focus:ring-[#646cff]/20 transition-all"
                    placeholder="Enter your email"
                    required
                  />
                </div>
              </div>

              <div>
                <label className="flex items-center gap-1 mb-2 text-gray-300 text-sm">
                  Password
                  <span className="text-red-400">*</span>
                </label>
                <div className="relative">
                  <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                  <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    onFocus={() => mode === 'register' && setPasswordFocused(true)}
                    onBlur={() => setPasswordFocused(false)}
                    className="w-full pl-10 pr-4 py-3 bg-[rgba(15,23,42,0.6)] border border-white/10 rounded-xl text-white placeholder-gray-500 focus:outline-none focus:border-[#646cff] focus:ring-4 focus:ring-[#646cff]/20 transition-all"
                    placeholder="Enter your password"
                    required
                  />
                </div>
                
                {/* Password Strength Hints */}
                {mode === 'register' && (passwordFocused || password.length > 0) && (
                  <div className="mt-3 p-3 bg-[rgba(15,23,42,0.8)] border border-white/10 rounded-xl space-y-2">
                    <div className="text-xs text-gray-400 mb-2">Password must contain:</div>
                    {passwordRequirements.map((req) => {
                      const isMet = req.test(password);
                      return (
                        <div 
                          key={req.id} 
                          className="flex items-center space-x-2 text-xs transition-all duration-300"
                        >
                          <div className={`flex-shrink-0 w-4 h-4 rounded-full flex items-center justify-center transition-all duration-300 ${
                            isMet 
                              ? 'bg-emerald-500/20 border border-emerald-500' 
                              : 'bg-white/5 border border-white/20'
                          }`}>
                            {isMet && <Check className="w-3 h-3 text-emerald-400" />}
                          </div>
                          <span className={`transition-colors duration-300 ${
                            isMet ? 'text-emerald-400' : 'text-gray-500'
                          }`}>
                            {req.text}
                          </span>
                        </div>
                      );
                    })}
                  </div>
                )}
              </div>

              {mode === 'register' && (
                <div>
                  <label className="flex items-center gap-1 mb-2 text-gray-300 text-sm">
                    Confirm Password
                    <span className="text-red-400">*</span>
                  </label>
                  <div className="relative">
                    <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                    <input
                      type="password"
                      value={confirmPassword}
                      onChange={(e) => setConfirmPassword(e.target.value)}
                      className={`w-full pl-10 pr-4 py-3 bg-[rgba(15,23,42,0.6)] border rounded-xl text-white placeholder-gray-500 focus:outline-none focus:ring-4 transition-all ${
                        confirmPassword.length > 0
                          ? password === confirmPassword
                            ? 'border-emerald-500 focus:border-emerald-500 focus:ring-emerald-500/20'
                            : 'border-red-500 focus:border-red-500 focus:ring-red-500/20'
                          : 'border-white/10 focus:border-[#646cff] focus:ring-[#646cff]/20'
                      }`}
                      placeholder="Confirm your password"
                      required
                    />
                  </div>
                  {confirmPassword.length > 0 && (
                    <div className="mt-2 flex items-center space-x-2">
                      <div className={`flex-shrink-0 w-4 h-4 rounded-full flex items-center justify-center transition-all duration-300 ${
                        password === confirmPassword
                          ? 'bg-emerald-500/20 border border-emerald-500'
                          : 'bg-red-500/20 border border-red-500'
                      }`}>
                        {password === confirmPassword && <Check className="w-3 h-3 text-emerald-400" />}
                      </div>
                      <span className={`text-xs transition-colors duration-300 ${
                        password === confirmPassword ? 'text-emerald-400' : 'text-red-400'
                      }`}>
                        {password === confirmPassword ? 'Passwords match' : 'Passwords do not match'}
                      </span>
                    </div>
                  )}
                </div>
              )}

              {mode === 'login' && (
                <div className="text-right -mt-2">
                  <button
                    type="button"
                    onClick={() => setMode('forgotPassword')}
                    className="text-sm text-gray-400 hover:text-[#646cff] transition-colors"
                  >
                    Forgot password?
                  </button>
                </div>
              )}

              {mode === 'register' && (
                <div className="p-4 bg-[#646cff]/10 border border-[#646cff]/30 rounded-xl">
                  <div className="flex items-start space-x-2">
                    <Shield className="w-5 h-5 text-[#646cff] flex-shrink-0 mt-0.5" />
                    <p className="text-sm text-gray-300">
                      You'll need to verify your identity to participate in competitions and
                      withdraw funds
                    </p>
                  </div>
                </div>
              )}

              <button
                type="submit"
                className="w-full py-3 bg-[#646cff] text-white rounded-xl hover:bg-[#535bf2] transition-all shadow-lg shadow-[#646cff]/30"
              >
                {mode === 'login' ? 'Sign In' : 'Create Account'}
              </button>
            </form>

            <div className="mt-6 pt-6 border-t border-white/10">
              <button
                onClick={() => setMode(mode === 'login' ? 'register' : 'login')}
                className="w-full py-3 bg-transparent text-gray-300 border border-white/20 rounded-xl hover:border-[#646cff] hover:text-white transition-all"
              >
                {mode === 'login'
                  ? "Don't have an account? Sign up"
                  : 'Already have an account? Sign in'}
              </button>
            </div>

            <div className="mt-6 text-center">
              <p className="text-xs text-gray-500">
                By continuing, you agree to our Terms of Service and Privacy Policy
              </p>
            </div>
          </>
        )}
      </div>
    </div>
  );
}