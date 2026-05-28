import { Dices } from 'lucide-react';

interface HomeProps {
  user: any;
  onNavigate: (page: string) => void;
}

export function Home({ user, onNavigate }: HomeProps) {
  const systemStatus = {
    status: "UP",
    message: "Lottery API v1 is running smoothly"
  };

  return (
    <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center px-4">
      <div className="max-w-4xl w-full text-center">
        {/* Main Heading */}
        <div className="mb-6">
          <h1 className="text-5xl md:text-6xl mb-4">
            <span className="text-[#eab308]">BIG WIN</span>{' '}
            <span className="text-white">LOTTERY</span>{' '}
            <span className="inline-block">
              <Dices className="w-12 h-12 md:w-14 md:h-14 text-[#eab308] inline" />
            </span>
          </h1>
          <p className="text-gray-400 text-lg">
            Test your luck and win big prizes today!
          </p>
        </div>

        {/* System Status Box */}
        <div className="mt-12 mb-8">
          <div className="inline-block">
            <div className="text-sm text-gray-400 mb-3 tracking-wider">SYSTEM STATUS</div>
            <div className="bg-[rgba(15,23,42,0.8)] border border-white/20 rounded-xl px-6 py-4 font-mono text-left">
              <pre className="text-[#22c55e] text-sm whitespace-pre-wrap">
                {JSON.stringify(systemStatus, null, 2)}
              </pre>
            </div>
          </div>
        </div>

        {/* Welcome Message */}
        <div className="mt-8">
          <p className="text-gray-400">
            Welcome back, <span className="text-[#eab308]">{user?.username}</span>!
          </p>
        </div>
      </div>
    </div>
  );
}
