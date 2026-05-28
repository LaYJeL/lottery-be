import { Trophy, Ticket, ListChecks, Star, Shield, TrendingUp } from 'lucide-react';

interface WelcomeProps {
  onGetStarted: () => void;
}

export function Welcome({ onGetStarted }: WelcomeProps) {
  return (
    <div className="min-h-screen" style={{ background: 'radial-gradient(circle at top right, #1e293b, #0f172a)' }}>
      {/* Hero Section */}
      <div className="flex items-center justify-center min-h-screen p-4">
        <div className="max-w-6xl w-full">
          <div className="text-center mb-12">
            <div className="inline-flex items-center justify-center w-20 h-20 bg-[#646cff] rounded-2xl mb-6 animate-bounce shadow-lg shadow-[#646cff]/50">
              <Trophy className="w-10 h-10 text-white" />
            </div>
            <h1 className="text-5xl mb-4 text-white">
              Welcome to LuckyPlay
            </h1>
            <p className="text-xl text-gray-300 mb-8 max-w-2xl mx-auto">
              Your gateway to exciting competitions, lotteries, and rewards. Join thousands of players winning amazing prizes every day!
            </p>
            <button
              onClick={onGetStarted}
              className="px-8 py-4 bg-[#646cff] text-white rounded-xl hover:bg-[#535bf2] transition-all transform hover:scale-105 shadow-lg shadow-[#646cff]/30"
            >
              Get Started Now
            </button>
          </div>

          {/* Features Grid */}
          <div className="grid md:grid-cols-3 gap-6 mt-16">
            <div className="bg-[rgba(30,41,59,0.7)] backdrop-blur-xl p-8 rounded-2xl border border-white/10 hover:border-[#646cff]/50 transition-all">
              <div className="w-12 h-12 bg-[#646cff]/20 rounded-xl flex items-center justify-center mb-4">
                <Ticket className="w-6 h-6 text-[#646cff]" />
              </div>
              <h3 className="text-xl mb-2 text-white">Lottery Tickets</h3>
              <p className="text-gray-400">
                Purchase tickets for daily, weekly, and special lottery draws with amazing jackpots
              </p>
            </div>
            <div className="bg-[rgba(30,41,59,0.7)] backdrop-blur-xl p-8 rounded-2xl border border-white/10 hover:border-[#646cff]/50 transition-all">
              <div className="w-12 h-12 bg-[#646cff]/20 rounded-xl flex items-center justify-center mb-4">
                <Trophy className="w-6 h-6 text-[#646cff]" />
              </div>
              <h3 className="text-xl mb-2 text-white">Competitions</h3>
              <p className="text-gray-400">
                Join exciting competitions and showcase your talents to win incredible prizes
              </p>
            </div>
            <div className="bg-[rgba(30,41,59,0.7)] backdrop-blur-xl p-8 rounded-2xl border border-white/10 hover:border-[#646cff]/50 transition-all">
              <div className="w-12 h-12 bg-[#646cff]/20 rounded-xl flex items-center justify-center mb-4">
                <ListChecks className="w-6 h-6 text-[#646cff]" />
              </div>
              <h3 className="text-xl mb-2 text-white">Build Reputation</h3>
              <p className="text-gray-400">
                Complete tasks to improve your account reputation and unlock exclusive rewards
              </p>
            </div>
          </div>

          {/* Why Choose Us */}
          <div className="mt-24 mb-12">
            <h2 className="text-3xl text-center mb-12 text-white">Why Choose LuckyPlay?</h2>
            <div className="grid md:grid-cols-3 gap-8">
              <div className="text-center">
                <div className="inline-flex items-center justify-center w-16 h-16 bg-emerald-500/20 rounded-full mb-4">
                  <Shield className="w-8 h-8 text-emerald-400" />
                </div>
                <h3 className="text-xl mb-2 text-white">Secure & Verified</h3>
                <p className="text-gray-400">
                  Complete identity verification for safe transactions and fair play
                </p>
              </div>
              <div className="text-center">
                <div className="inline-flex items-center justify-center w-16 h-16 bg-amber-500/20 rounded-full mb-4">
                  <Star className="w-8 h-8 text-amber-400" />
                </div>
                <h3 className="text-xl mb-2 text-white">Reputation System</h3>
                <p className="text-gray-400">
                  Earn reputation points and unlock exclusive features and better odds
                </p>
              </div>
              <div className="text-center">
                <div className="inline-flex items-center justify-center w-16 h-16 bg-cyan-500/20 rounded-full mb-4">
                  <TrendingUp className="w-8 h-8 text-cyan-400" />
                </div>
                <h3 className="text-xl mb-2 text-white">Easy Payments</h3>
                <p className="text-gray-400">
                  Quick deposits and withdrawals with multiple payment methods
                </p>
              </div>
            </div>
          </div>

          {/* CTA */}
          <div className="bg-[rgba(30,41,59,0.7)] backdrop-blur-xl border border-white/10 p-12 rounded-2xl text-center mt-16">
            <h2 className="text-3xl mb-4 text-white">Ready to Start Winning?</h2>
            <p className="text-gray-300 mb-6 text-lg max-w-2xl mx-auto">
              Join our community today and get access to exclusive competitions, lotteries, and daily rewards!
            </p>
            <button
              onClick={onGetStarted}
              className="px-8 py-4 bg-[#646cff] text-white rounded-xl hover:bg-[#535bf2] transition-all transform hover:scale-105 shadow-lg shadow-[#646cff]/30"
            >
              Create Your Account
            </button>
          </div>
        </div>
      </div>

      {/* Footer */}
      <footer className="bg-[rgba(30,41,59,0.7)] backdrop-blur-xl border-t border-white/10 py-8">
        <div className="max-w-6xl mx-auto px-4 text-center text-gray-400">
          <p>&copy; 2024 LuckyPlay Platform. All rights reserved.</p>
        </div>
      </footer>
    </div>
  );
}
