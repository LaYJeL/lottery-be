import { useState } from 'react';
import { Welcome } from './components/Welcome';
import { Home } from './components/Home';
import { Dashboard } from './components/Dashboard';
import { Lotteries } from './components/Lotteries';
import { Competitions } from './components/Competitions';
import { Tasks } from './components/Tasks';
import { Wallet } from './components/Wallet';
import { Profile } from './components/Profile';
import { AuthModal } from './components/AuthModal';
import { Home as HomeIcon, Ticket, Trophy, ListChecks, Wallet as WalletIcon, User, LogOut, LayoutDashboard } from 'lucide-react';

function App() {
  const [currentPage, setCurrentPage] = useState('home');
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [showAuthModal, setShowAuthModal] = useState(false);
  const [user, setUser] = useState<any>(null);

  // Mock user data for demonstration
  const mockUser = {
    id: '1',
    username: 'JohnDoe',
    email: 'john@example.com',
    verified: true,
    reputation: 850,
    balance: 1250.50,
    joinedDate: '2024-01-15'
  };

  const handleLogin = (email: string, password: string) => {
    // In production, this would call your Java backend API
    console.log('Login attempt:', email, password);
    setIsAuthenticated(true);
    setUser(mockUser);
    setShowAuthModal(false);
    setCurrentPage('home');
  };

  const handleRegister = (username: string, email: string, password: string) => {
    // In production, this would call your Java backend API
    console.log('Register attempt:', username, email, password);
    setIsAuthenticated(true);
    setUser(mockUser);
    setShowAuthModal(false);
    setCurrentPage('home');
  };

  const handleLogout = () => {
    setIsAuthenticated(false);
    setUser(null);
    setCurrentPage('home');
  };

  const navItems = [
    { id: 'home', label: 'Home', icon: HomeIcon },
    { id: 'dashboard', label: 'Dashboard', icon: LayoutDashboard },
    { id: 'lotteries', label: 'Lotteries', icon: Ticket },
    { id: 'competitions', label: 'Competitions', icon: Trophy },
    { id: 'tasks', label: 'Tasks', icon: ListChecks },
    { id: 'wallet', label: 'Wallet', icon: WalletIcon },
    { id: 'profile', label: 'Profile', icon: User },
  ];

  const renderPage = () => {
    if (!isAuthenticated) {
      return <Welcome onGetStarted={() => setShowAuthModal(true)} />;
    }

    switch (currentPage) {
      case 'home':
        return <Home user={user} onNavigate={setCurrentPage} />;
      case 'dashboard':
        return <Dashboard user={user} />;
      case 'lotteries':
        return <Lotteries user={user} />;
      case 'competitions':
        return <Competitions user={user} />;
      case 'tasks':
        return <Tasks user={user} />;
      case 'wallet':
        return <Wallet user={user} />;
      case 'profile':
        return <Profile user={user} />;
      default:
        return <Home user={user} onNavigate={setCurrentPage} />;
    }
  };

  return (
    <div className="min-h-screen" style={{ background: 'radial-gradient(circle at top right, #1e293b, #0f172a)' }}>
      {isAuthenticated && (
        <nav className="bg-[rgba(30,41,59,0.7)] backdrop-blur-xl border-b border-white/10">
          <div className="max-w-7xl mx-auto px-4">
            <div className="flex items-center justify-between h-16">
              <div className="flex items-center space-x-8">
                <div className="flex items-center space-x-2">
                  <div className="w-10 h-10 bg-[#646cff] rounded-xl flex items-center justify-center">
                    <Trophy className="w-6 h-6 text-white" />
                  </div>
                  <span className="text-xl text-white">
                    LuckyPlay
                  </span>
                </div>
                <div className="hidden md:flex space-x-1">
                  {navItems.map((item) => {
                    const Icon = item.icon;
                    return (
                      <button
                        key={item.id}
                        onClick={() => setCurrentPage(item.id)}
                        className={`flex items-center space-x-2 px-4 py-2 rounded-lg transition-colors ${
                          currentPage === item.id
                            ? 'bg-[#646cff] text-white'
                            : 'text-gray-300 hover:bg-white/5'
                        }`}
                      >
                        <Icon className="w-5 h-5" />
                        <span>{item.label}</span>
                      </button>
                    );
                  })}
                </div>
              </div>
              <div className="flex items-center space-x-4">
                <div className="text-right hidden sm:block">
                  <div className="text-sm text-gray-300">{user?.username}</div>
                  <div className="text-xs text-gray-400">
                    Balance: ${user?.balance.toFixed(2)}
                  </div>
                </div>
                <button
                  onClick={handleLogout}
                  className="p-2 text-gray-300 hover:bg-white/5 rounded-lg transition-colors"
                  title="Logout"
                >
                  <LogOut className="w-5 h-5" />
                </button>
              </div>
            </div>
          </div>
        </nav>
      )}

      <main className={isAuthenticated ? 'max-w-7xl mx-auto px-4 py-8' : ''}>
        {renderPage()}
      </main>

      {showAuthModal && (
        <AuthModal
          onClose={() => setShowAuthModal(false)}
          onLogin={handleLogin}
          onRegister={handleRegister}
        />
      )}
    </div>
  );
}

export default App;