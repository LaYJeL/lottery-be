import { User, Mail, Calendar, Shield, Star, Settings, Edit2 } from 'lucide-react';

interface ProfileProps {
  user: any;
}

export function Profile({ user }: ProfileProps) {
  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-3xl mb-2">Profile</h1>
        <p className="text-gray-600">Manage your account settings and preferences</p>
      </div>

      {/* Profile Header */}
      <div className="bg-gradient-to-r from-indigo-600 to-purple-600 p-8 rounded-2xl text-white">
        <div className="flex items-center space-x-6">
          <div className="w-24 h-24 bg-white/20 rounded-2xl flex items-center justify-center">
            <User className="w-12 h-12" />
          </div>
          <div className="flex-1">
            <div className="flex items-center space-x-3 mb-2">
              <h2 className="text-2xl">{user?.username}</h2>
              {user?.verified && (
                <div className="p-1 bg-white/20 rounded-full" title="Verified">
                  <Shield className="w-5 h-5" />
                </div>
              )}
            </div>
            <div className="text-indigo-100 mb-4">{user?.email}</div>
            <div className="flex items-center space-x-6 text-sm">
              <div>
                <div className="text-indigo-100">Reputation</div>
                <div className="flex items-center space-x-1">
                  <Star className="w-4 h-4 fill-white" />
                  <span>{user?.reputation}</span>
                </div>
              </div>
              <div>
                <div className="text-indigo-100">Balance</div>
                <div>${user?.balance.toFixed(2)}</div>
              </div>
              <div>
                <div className="text-indigo-100">Member Since</div>
                <div>
                  {new Date(user?.joinedDate).toLocaleDateString('en-US', {
                    month: 'short',
                    year: 'numeric',
                  })}
                </div>
              </div>
            </div>
          </div>
          <button className="p-3 bg-white/20 rounded-xl hover:bg-white/30 transition-colors">
            <Edit2 className="w-5 h-5" />
          </button>
        </div>
      </div>

      {/* Account Information */}
      <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-xl">Account Information</h2>
          <Settings className="w-5 h-5 text-gray-400" />
        </div>
        <div className="space-y-4">
          <div className="flex items-center justify-between p-4 bg-gray-50 rounded-xl">
            <div className="flex items-center space-x-4">
              <div className="p-2 bg-white rounded-lg">
                <User className="w-5 h-5 text-gray-600" />
              </div>
              <div>
                <div className="text-sm text-gray-600">Username</div>
                <div>{user?.username}</div>
              </div>
            </div>
            <button className="text-sm text-indigo-600 hover:text-indigo-700">Edit</button>
          </div>
          <div className="flex items-center justify-between p-4 bg-gray-50 rounded-xl">
            <div className="flex items-center space-x-4">
              <div className="p-2 bg-white rounded-lg">
                <Mail className="w-5 h-5 text-gray-600" />
              </div>
              <div>
                <div className="text-sm text-gray-600">Email</div>
                <div>{user?.email}</div>
              </div>
            </div>
            <button className="text-sm text-indigo-600 hover:text-indigo-700">Edit</button>
          </div>
          <div className="flex items-center justify-between p-4 bg-gray-50 rounded-xl">
            <div className="flex items-center space-x-4">
              <div className="p-2 bg-white rounded-lg">
                <Calendar className="w-5 h-5 text-gray-600" />
              </div>
              <div>
                <div className="text-sm text-gray-600">Member Since</div>
                <div>
                  {new Date(user?.joinedDate).toLocaleDateString('en-US', {
                    month: 'long',
                    day: 'numeric',
                    year: 'numeric',
                  })}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Verification Status */}
      <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm">
        <h2 className="text-xl mb-4">Verification Status</h2>
        <div className="space-y-3">
          <div className="flex items-center justify-between p-4 bg-green-50 border border-green-200 rounded-xl">
            <div className="flex items-center space-x-4">
              <div className="p-2 bg-white rounded-lg">
                <Mail className="w-5 h-5 text-green-600" />
              </div>
              <div>
                <div>Email Verified</div>
                <div className="text-sm text-gray-600">Your email has been confirmed</div>
              </div>
            </div>
            <div className="px-3 py-1 bg-green-100 text-green-700 rounded-full text-sm">
              Verified
            </div>
          </div>
          <div className="flex items-center justify-between p-4 bg-yellow-50 border border-yellow-200 rounded-xl">
            <div className="flex items-center space-x-4">
              <div className="p-2 bg-white rounded-lg">
                <Shield className="w-5 h-5 text-yellow-600" />
              </div>
              <div>
                <div>Identity Verification</div>
                <div className="text-sm text-gray-600">Complete KYC to unlock all features</div>
              </div>
            </div>
            <button className="px-4 py-2 bg-yellow-600 text-white rounded-lg hover:bg-yellow-700 text-sm">
              Verify Now
            </button>
          </div>
        </div>
      </div>

      {/* Statistics */}
      <div className="grid md:grid-cols-2 gap-6">
        <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm">
          <h2 className="text-xl mb-4">Activity Statistics</h2>
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <span className="text-gray-600">Lottery Tickets Purchased</span>
              <span className="text-xl">47</span>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-gray-600">Competition Entries</span>
              <span className="text-xl">12</span>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-gray-600">Tasks Completed</span>
              <span className="text-xl">156</span>
            </div>
            <div className="flex items-center justify-between">
              <span className="text-gray-600">Total Winnings</span>
              <span className="text-xl text-green-600">$850</span>
            </div>
          </div>
        </div>

        <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm">
          <h2 className="text-xl mb-4">Account Level</h2>
          <div className="text-center mb-6">
            <div className="text-4xl mb-2">🏆</div>
            <div className="text-2xl mb-1">Gold Member</div>
            <div className="text-sm text-gray-600">Level 3</div>
          </div>
          <div className="space-y-2">
            <div className="flex items-center justify-between text-sm">
              <span className="text-gray-600">Progress to Platinum</span>
              <span>75%</span>
            </div>
            <div className="h-3 bg-gray-100 rounded-full overflow-hidden">
              <div
                className="h-full bg-gradient-to-r from-indigo-600 to-purple-600 rounded-full"
                style={{ width: '75%' }}
              />
            </div>
            <div className="text-sm text-gray-600 text-center">150 more points needed</div>
          </div>
        </div>
      </div>

      {/* Security Settings */}
      <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm">
        <h2 className="text-xl mb-4">Security</h2>
        <div className="space-y-3">
          <button className="w-full flex items-center justify-between p-4 bg-gray-50 rounded-xl hover:bg-gray-100 transition-colors">
            <div className="text-left">
              <div>Change Password</div>
              <div className="text-sm text-gray-600">Update your account password</div>
            </div>
            <Edit2 className="w-5 h-5 text-gray-400" />
          </button>
          <button className="w-full flex items-center justify-between p-4 bg-gray-50 rounded-xl hover:bg-gray-100 transition-colors">
            <div className="text-left">
              <div>Two-Factor Authentication</div>
              <div className="text-sm text-gray-600">
                Add an extra layer of security
              </div>
            </div>
            <div className="px-3 py-1 bg-gray-200 text-gray-700 rounded-full text-sm">
              Disabled
            </div>
          </button>
        </div>
      </div>

      {/* Preferences */}
      <div className="bg-white p-6 rounded-2xl border border-gray-100 shadow-sm">
        <h2 className="text-xl mb-4">Preferences</h2>
        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <div>Email Notifications</div>
              <div className="text-sm text-gray-600">Receive updates via email</div>
            </div>
            <label className="relative inline-flex items-center cursor-pointer">
              <input type="checkbox" className="sr-only peer" defaultChecked />
              <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-indigo-300 rounded-full peer peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-indigo-600"></div>
            </label>
          </div>
          <div className="flex items-center justify-between">
            <div>
              <div>Marketing Communications</div>
              <div className="text-sm text-gray-600">Promotional offers and news</div>
            </div>
            <label className="relative inline-flex items-center cursor-pointer">
              <input type="checkbox" className="sr-only peer" />
              <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-indigo-300 rounded-full peer peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-indigo-600"></div>
            </label>
          </div>
        </div>
      </div>
    </div>
  );
}
